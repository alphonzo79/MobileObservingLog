/*
 * Copyright (c) 2012 Joe Rowley
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.mobileobservinglog.support.billing;

import org.json.JSONException;

import com.android.vending.billing.IInAppBillingService;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.mobileobservinglog.support.billing.PurchaseSecurity;

//This class is adapted from the TrivialDrive example provided by Google. Much of the code is taken straight out of that example
public class DonationBillingHandler {
	Context context;
	String billingKey;
	boolean setupDone = false;
	boolean asyncInProgress = false;
	String asyncOperation;
	int requestCode;
	ServiceConnection serviceConn;
	IInAppBillingService service;
	OnPurchaseFinishedListener purchaseListener;
	
	// Billing response codes
    public static final int BILLING_RESPONSE_RESULT_OK = 0;
    public static final int BILLING_RESPONSE_RESULT_USER_CANCELED = 1;
    public static final int BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3;
    public static final int BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4;
    public static final int BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5;
    public static final int BILLING_RESPONSE_RESULT_ERROR = 6;
    public static final int BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7;
    public static final int BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED = 8;

    // Billing Helper error codes
    public static final int BILLINGHELPER_ERROR_BASE = -1000;
    public static final int BILLINGHELPER_REMOTE_EXCEPTION = -1001;
    public static final int BILLINGHELPER_BAD_RESPONSE = -1002;
    public static final int BILLINGHELPER_VERIFICATION_FAILED = -1003;
    public static final int BILLINGHELPER_SEND_INTENT_FAILED = -1004;
    public static final int BILLINGHELPER_USER_CANCELLED = -1005;
    public static final int BILLINGHELPER_UNKNOWN_PURCHASE_RESPONSE = -1006;
    public static final int BILLINGHELPER_MISSING_TOKEN = -1007;
    public static final int BILLINGHELPER_UNKNOWN_ERROR = -1008;

    // Keys for the responses from InAppBillingService
    public static final String RESPONSE_CODE = "RESPONSE_CODE";
    public static final String RESPONSE_GET_SKU_DETAILS_LIST = "DETAILS_LIST";
    public static final String RESPONSE_BUY_INTENT = "BUY_INTENT";
    public static final String RESPONSE_INAPP_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
    public static final String RESPONSE_INAPP_SIGNATURE = "INAPP_DATA_SIGNATURE";
    public static final String RESPONSE_INAPP_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST";
    public static final String RESPONSE_INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
    public static final String RESPONSE_INAPP_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST";
    public static final String INAPP_CONTINUATION_TOKEN = "INAPP_CONTINUATION_TOKEN";
 
    // Item type: in-app item
    public static final String ITEM_TYPE_INAPP = "inapp";

    // some fields on the getSkuDetails response bundle
    public static final String GET_SKU_DETAILS_ITEM_LIST = "ITEM_ID_LIST";
    public static final String GET_SKU_DETAILS_ITEM_TYPE_LIST = "ITEM_TYPE_LIST";
	
	public DonationBillingHandler(Context context, String appKey) {
		this.context = context.getApplicationContext();
		billingKey = appKey;
	}
	
	public interface OnSetupFinishedListener {
        /**
         * Called to notify that setup is complete.
         *
         * @param result The result of the setup process.
         */
        public void onSetupFinished(BillingHandlerResult result);
    }
	
	public void startSetup(final OnSetupFinishedListener listener) {
		// If already set up, can't do it again.
        if (setupDone) {
			Log.d("InAppPurchase", "Setup was already done -- handler");
        	return;
        }
        
        serviceConn = new ServiceConnection() {
            public void onServiceDisconnected(ComponentName name) {
                service = null;
            }

            public void onServiceConnected(ComponentName name, IBinder bindService) {
                service = IInAppBillingService.Stub.asInterface(bindService);
                String packageName = context.getPackageName();
                try {
                    //Checking for in-app billing 3 support.
                    int response = service.isBillingSupported(3, packageName, ITEM_TYPE_INAPP);
                    if (response != BILLING_RESPONSE_RESULT_OK) {
                        if (listener != null) listener.onSetupFinished(new BillingHandlerResult(response,
                                "Error checking for billing v3 support."));
                        return;
                    }
                    setupDone = true;
                }
                catch (RemoteException e) {
                    if (listener != null) {
                        listener.onSetupFinished(new BillingHandlerResult(BILLINGHELPER_REMOTE_EXCEPTION,
                                                    "RemoteException while setting up in-app billing."));
                    }
                    e.printStackTrace();
                }

                if (listener != null) {
                    listener.onSetupFinished(new BillingHandlerResult(BILLING_RESPONSE_RESULT_OK, "Setup successful."));
                }
            }
        };
        context.bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"),
                             serviceConn, Context.BIND_AUTO_CREATE);
    }
	
	public static String getResponseDesc(int code) {
        String[] billing_msgs = ("0:OK/1:User Canceled/2:Unknown/" +
                "3:Billing Unavailable/4:Item unavailable/" +
                "5:Developer Error/6:Error/7:Item Already Owned/" +
                "8:Item not owned").split("/");
        String[] billingHelper_msgs = ("0:OK/-1001:Remote exception during initialization/" +
                                   "-1002:Bad response received/" +
                                   "-1003:Purchase signature verification failed/" +
                                   "-1004:Send intent failed/" +
                                   "-1005:User cancelled/" +
                                   "-1006:Unknown purchase response/" +
                                   "-1007:Missing token/" +
                                   "-1008:Unknown error").split("/");

        if (code <= BILLINGHELPER_ERROR_BASE) {
            int index = BILLINGHELPER_ERROR_BASE - code;
            if (index >= 0 && index < billingHelper_msgs.length) return billingHelper_msgs[index];
            else return String.valueOf(code) + ":Unknown IAB Helper Error";
        }
        else if (code < 0 || code >= billing_msgs.length)
            return String.valueOf(code) + ":Unknown";
        else
            return billing_msgs[code];
    }
	
	public void dispose() {
        setupDone = false;
        if (serviceConn != null) {
            if (context != null) context.unbindService(serviceConn);
            serviceConn = null;
            service = null;
            purchaseListener = null;
        }
    }
	
	/**
     * Callback that notifies when a purchase is finished.
     */
    public interface OnPurchaseFinishedListener {
        /**
         * Called to notify that an in-app purchase finished. If the purchase was successful,
         * then the sku parameter specifies which item was purchased. If the purchase failed,
         * the sku and extraData parameters may or may not be null, depending on how far the purchase
         * process went.
         *
         * @param result The result of the purchase.
         * @param info The purchase information (null if purchase failed)
         */
        public void onPurchaseFinished(BillingHandlerResult result, Purchase info);
    }
    
    /**
     * Same as calling {@link #launchPurchaseFlow(Activity, String, int, OnIabPurchaseFinishedListener, String)}
     * with null as extraData.
     */
    public void launchPurchaseFlow(Activity act, String sku, int requestCode, OnPurchaseFinishedListener listener) {
        launchPurchaseFlow(act, sku, requestCode, listener, "");
    }

    /**
     * Initiate the UI flow for an in-app purchase. Call this method to initiate an in-app purchase,
     * which will involve bringing up the Google Play screen. The calling activity will be paused while
     * the user interacts with Google Play, and the result will be delivered via the activity's
     * {@link android.app.Activity#onActivityResult} method, at which point you must call
     * this object's {@link #handleActivityResult} method to continue the purchase flow. This method
     * MUST be called from the UI thread of the Activity.
     *
     * @param act The calling activity.
     * @param sku The sku of the item to purchase.
     * @param requestCode A request code (to differentiate from other responses --
     *     as in {@link android.app.Activity#startActivityForResult}).
     * @param listener The listener to notify when the purchase process finishes
     * @param extraData Extra data (developer payload), which will be returned with the purchase data
     *     when the purchase completes. This extra data will be permanently bound to that purchase
     *     and will always be returned when the purchase is queried.
     */
    public void launchPurchaseFlow(Activity act, String sku, int requestCode, OnPurchaseFinishedListener listener, String extraData) {
		Log.d("InAppPurchase", "Purchase Flow started -- handler");
        BillingHandlerResult result;
        purchaseListener = listener;
        try {
        	checkSetupDone("launchPurchaseFlow");
        } catch (IllegalStateException e) {
			Log.d("InAppPurchase", "Setup was not done -- handler");
        	result = new BillingHandlerResult(BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE, "Setup was not finished yet");
        	if (listener != null) listener.onPurchaseFinished(result, null);
        	return;
        }
        flagStartAsync("launchPurchaseFlow");

        try {
			Log.d("InAppPurchase", "Starting the purchase try -- handler");
            Bundle buyIntentBundle = service.getBuyIntent(3, context.getPackageName(), sku, ITEM_TYPE_INAPP, extraData);
            int response = getResponseCodeFromBundle(buyIntentBundle);
            if (response != BILLING_RESPONSE_RESULT_OK) {
    			Log.d("InAppPurchase", "The response code from the bundle was not OK -- handler");

    	        
    	        //In case we have a managed item that needs to be consumed. Bit of a hack. But works for now
    			if(response == BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {
	    			String packageName = context.getPackageName();
	    	        service.consumePurchase(3,  packageName, "inapp:"+packageName+":"+sku);
	    	        buyIntentBundle = service.getBuyIntent(3, packageName, sku, ITEM_TYPE_INAPP, extraData);
    			}
            }

            PendingIntent pendingIntent = buyIntentBundle.getParcelable(RESPONSE_BUY_INTENT);
            this.requestCode = requestCode;
			Log.d("InAppPurchase", "About to start the intent sender for result -- handler");
			if(pendingIntent != null) {
	            act.startIntentSenderForResult(pendingIntent.getIntentSender(),
	                                           requestCode, new Intent(),
	                                           Integer.valueOf(0), Integer.valueOf(0),
	                                           Integer.valueOf(0));
				Log.d("InAppPurchase", "Done with the intent sender for result -- handler");
			} else {
				result = new BillingHandlerResult(BILLINGHELPER_SEND_INTENT_FAILED, "Failed to send intent. We had a null pending intent");
	            if (purchaseListener != null) purchaseListener.onPurchaseFinished(result, null);
			}
        }
        catch (SendIntentException e) {
			Log.d("InAppPurchase", "Caught a SendIntentException -- handler");
            e.printStackTrace();

            result = new BillingHandlerResult(BILLINGHELPER_SEND_INTENT_FAILED, "Failed to send intent.");
            if (purchaseListener != null) purchaseListener.onPurchaseFinished(result, null);
        }
        catch (RemoteException e) {
			Log.d("InAppPurchase", "Caught RemoteException -- handler");
            e.printStackTrace();

            result = new BillingHandlerResult(BILLINGHELPER_REMOTE_EXCEPTION, "Remote exception while starting purchase flow");
            if (purchaseListener != null) purchaseListener.onPurchaseFinished(result, null);
        }
    }
    
    /**
     * Handles an activity result that's part of the purchase flow in in-app billing. If you
     * are calling {@link #launchPurchaseFlow}, then you must call this method from your
     * Activity's {@link android.app.Activity@onActivityResult} method. This method
     * MUST be called from the UI thread of the Activity.
     *
     * @param requestCode The requestCode as you received it.
     * @param resultCode The resultCode as you received it.
     * @param data The data (Intent) as you received it.
     * @return Returns true if the result was related to a purchase flow and was handled;
     *     false if the result was not related to a purchase, in which case you should
     *     handle it normally.
     */
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("InAppPurchase", "Starting HandleActivityResult -- handler");
        BillingHandlerResult result;
        if (requestCode != this.requestCode) return false;

        try {
        	checkSetupDone("handleActivityResult");
        } catch (IllegalStateException e) {
			Log.d("InAppPurchase", "Setup was not done -- handler");
        	result = new BillingHandlerResult(BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE, "Setup was not finished yet");
        }

        // end of async purchase operation
        flagEndAsync();

        if (data == null) {
			Log.d("InAppPurchase", "Data was null -- handler");
            result = new BillingHandlerResult(BILLINGHELPER_BAD_RESPONSE, "Null data in IAB result");
            if (purchaseListener != null) purchaseListener.onPurchaseFinished(result, null);
            return true;
        }

        int responseCode = getResponseCodeFromIntent(data);
        String purchaseData = data.getStringExtra(RESPONSE_INAPP_PURCHASE_DATA);
        String dataSignature = data.getStringExtra(RESPONSE_INAPP_SIGNATURE);

        if (resultCode == Activity.RESULT_OK && responseCode == BILLING_RESPONSE_RESULT_OK) {
			Log.d("InAppPurchase", "Activity.Result was OK and Billing response result was ok -- handler");
            if (purchaseData == null || dataSignature == null) {
    			Log.d("InAppPurchase", "But we had null data -- handler");
                result = new BillingHandlerResult(BILLINGHELPER_UNKNOWN_ERROR, "IAB returned null purchaseData or dataSignature");
                if (purchaseListener != null) purchaseListener.onPurchaseFinished(result, null);
                return true;
            }

            Purchase purchase = null;
            try {
    			Log.d("InAppPurchase", "Checking the purchase -- handler");
                purchase = new Purchase(purchaseData, dataSignature);
                String sku = purchase.getSku();

                // Verify signature
                if (!PurchaseSecurity.verifyPurchase(billingKey, purchaseData, dataSignature)) {
        			Log.d("InAppPurchase", "Uh Oh, Verification failed on the purchase -- handler");
                    result = new BillingHandlerResult(BILLINGHELPER_VERIFICATION_FAILED, "Signature verification failed for sku " + sku);
                    if (purchaseListener != null) purchaseListener.onPurchaseFinished(result, purchase);
                    return true;
                }
            }
            catch (JSONException e) {
    			Log.d("InAppPurchase", "Caught a JSON exception -- handler");
                e.printStackTrace();
                result = new BillingHandlerResult(BILLINGHELPER_BAD_RESPONSE, "Failed to parse purchase data.");
                if (purchaseListener != null) purchaseListener.onPurchaseFinished(result, null);
                return true;
            }

            if (purchaseListener != null) {
    			Log.d("InAppPurchase", "Setting the purchase listener to OK -- handler");
                purchaseListener.onPurchaseFinished(new BillingHandlerResult(BILLING_RESPONSE_RESULT_OK, "Success"), purchase);
            }
        }
        else if (resultCode == Activity.RESULT_OK) {
            // result code was OK, but in-app billing response was not OK.
            if (purchaseListener != null) {
    			Log.d("InAppPurchase", "Problem purchasing the item -- handler");
                result = new BillingHandlerResult(responseCode, "Problem purchashing item.");
                purchaseListener.onPurchaseFinished(result, null);
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
			Log.d("InAppPurchase", "Purchase was canceled -- handler");
            result = new BillingHandlerResult(BILLINGHELPER_USER_CANCELLED, "User canceled.");
            if (purchaseListener != null) purchaseListener.onPurchaseFinished(result, null);
        }
        else {
			Log.d("InAppPurchase", "this was an unknown purchase -- handler");
            result = new BillingHandlerResult(BILLINGHELPER_UNKNOWN_PURCHASE_RESPONSE, "Unknown purchase response.");
            if (purchaseListener != null) purchaseListener.onPurchaseFinished(result, null);
        }
        return true;
    }
    
 	// Checks that setup was done; if not, throws an exception.
    void checkSetupDone(String operation) {
        if (!setupDone) {
            throw new IllegalStateException("DonationBillingHandler helper is not set up. Can't perform operation: " + operation);
        }
    }
    
    // Workaround to bug where sometimes response codes come as Long instead of Integer
    int getResponseCodeFromBundle(Bundle b) {
        Object o = b.get(RESPONSE_CODE);
        if (o == null) {
            Log.d("JoeDebug", "Bundle with null response code, assuming OK (known issue)");
            return BILLING_RESPONSE_RESULT_OK;
        }
        else if (o instanceof Integer) return ((Integer)o).intValue();
        else if (o instanceof Long) return (int)((Long)o).longValue();
        else {
            Log.e("DonationBillingHandler", "Unexpected type for bundle response code.");
            Log.e("DonationBillingHandler", o.getClass().getName());
            throw new RuntimeException("Unexpected type for bundle response code: " + o.getClass().getName());
        }
    }

    // Workaround to bug where sometimes response codes come as Long instead of Integer
    int getResponseCodeFromIntent(Intent i) {
        Object o = i.getExtras().get(RESPONSE_CODE);
        if (o == null) {
        	Log.e("DonationBillingHandler", "Intent with no response code, assuming OK (known issue)");
            return BILLING_RESPONSE_RESULT_OK;
        }
        else if (o instanceof Integer) return ((Integer)o).intValue();
        else if (o instanceof Long) return (int)((Long)o).longValue();
        else {
        	Log.e("DonationBillingHandler", "Unexpected type for intent response code.");
        	Log.e("DonationBillingHandler", o.getClass().getName());
            throw new RuntimeException("Unexpected type for intent response code: " + o.getClass().getName());
        }
    }

    void flagStartAsync(String operation) {
    	BillingHandlerResult result;
        if (asyncInProgress) {
        	result = new BillingHandlerResult(BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE, "Another AsyncOp is already in progress");
        	if (purchaseListener != null) purchaseListener.onPurchaseFinished(result, null);
        	return;
        }
        asyncOperation = operation;
        asyncInProgress = true;
    }

    void flagEndAsync() {
        asyncOperation = "";
        asyncInProgress = false;
    }
}
