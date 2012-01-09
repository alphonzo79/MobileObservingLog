package com.rowley.mobileobservinglog;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.LayoutInflater.Factory;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class ActivityBase extends ListActivity implements View.OnClickListener{

	//Member Variables
	
	//Menu
	MenuItem toggleMenu;
	
	//Get access to the settings container singleton
	SettingsContainer settingsRef = SettingsContainer.getSettingsContainer();
	
	//Most of the activity life cycle methods are included here to be inherited by other classes to ensure that
	//button brightness will be set upon each resume and restored on each destroy and pause. OnCreate is not
	//included because we need to ensure that we can capture the existing setting to be restored on the Initial
	//screen launch. Since the super method is called first, including it here would destroy that setting before
	//we can capture it. That means that setDimButtons needs to be called in each child class
	
	@Override
    public void onPause() {
		Log.d("JoeDebug", "ActivityBase onPause");
        super.onPause();
		setDimButtons(settingsRef.getOriginalButtonBrightness());
    }

    @Override
    public void onDestroy() {
		Log.d("JoeDebug", "ActivityBase onDestroy");
        super.onDestroy();
		setDimButtons(settingsRef.getOriginalButtonBrightness());
    }

    @Override
    public void onResume() {
		Log.d("JoeDebug", "ActivityBase onResume");
        super.onResume();
		setDimButtons(settingsRef.getButtonBrightness());
    }    
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}
    
    //prepare the menu. This originally would have changed the mode button text, the icons and the text color according to the 
	//session mode, but the text color is handled in the inflator, which is only called on onCreateOptionsMenu. It causes problems
	//with the factory already being created if we do it in this method. That means that I currently do not have a way to change
	//the text color each time the menu displays. So I have commented out the lines that would have customized anything other
	//than the mode text.
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
		Log.d("JoeDebug", "ActivityBase preparing the menu");
		toggleMenu = menu.findItem(R.id.menuItem_toggleMode);
	    toggleMenu.setTitle(settingsRef.getModeButtonText());
	    return true;
    }
	
	//create the menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("JoeDebug", "ActivityBase creating the menu");
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(com.rowley.mobileobservinglog.R.menu.global_menu_night, menu);
	    setMenuBackground(menu);	
	    return true;
	}

	
	//the actions taken by each menu item
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.d("JoeDebug", "ActivityBase Options Menu Item pressed: " + item.getItemId());
    	Log.d("JoeDebug", "Content: " + this.getComponentName().toShortString());
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.menuItem_returnHome:
	    	//Check whether we are already on home screen. If not on home screen then navigate
	    	if (!this.getComponentName().toShortString().contains("HomeScreen"))
	    	{
	    		Intent settingsIntent = new Intent(this.getApplication(), HomeScreen.class);
	            startActivity(settingsIntent);
	    	}
	        return true;
	    case R.id.menuItem_toggleMode:
	        toggleMode();
	        return true;
	    case R.id.menuItem_settings:
	    	if (!this.getComponentName().toShortString().contains("SettingsScreen"))
	    	{
	    		Intent settingsIntent = new Intent(this.getApplication(), SettingsScreen.class);
	            startActivity(settingsIntent);
	    	}
	        return true;
	    case R.id.menuItem_info:
	    	if (!this.getComponentName().toShortString().contains("InfoScreen"))
	    	{
	    		Intent infoIntent = new Intent(this.getApplication(), InfoScreen.class);
	            startActivity(infoIntent);
		    }
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	//called by the Toggle Mode options menu item. Changes the layout references.
	private void toggleMode() {
		Log.d("JoeDebug", "ActivityBase toggle mode menu. Current session setting: " + settingsRef.getSessionMode());
		switch (settingsRef.getSessionMode()){
		case night:
			Log.d("JoeDebug", "ActivityBase setting normal mode");
			settingsRef.setNormalMode();
			break;
		case normal:
			settingsRef.setNightMode();
			Log.d("JoeDebug", "ActivityBase setting night mode");
			break;
		default:
			Log.d("JoeDebug", "ActivityBase default switch in toggleMode");
			break;
		}
		
		setLayout();
	}
	
	//Meant to be overridden by the calling classes to set their individual layouts. 
	public void setLayout(){
		
	}
	
	/**
	 * Set the buttons backlight level down to protect night vision.
	 * @param Float Value. -1.0f for no override, 0.0f to turn lights off 1.0f for full intensity
	 */
	public void setDimButtons(float val) {
		Window window = getWindow();
	    LayoutParams layoutParams = window.getAttributes();
	    if (layoutParams.buttonBrightness != val)
	    {
	    	try {
		        Log.d("JoeDebug", "Current button Brightness is " + layoutParams.buttonBrightness);
		        Log.d("JoeDebug", "Activity Base setDimButtons. Setting to " + val);
			    //layoutParams.buttonBrightness = val;
		        new BtnBrightness(layoutParams, val);
		    } catch (Exception e) {
		        Log.d("JoeDebug", "Failed to set button brightness");
		        e.printStackTrace();
		    }
		    window.setAttributes(layoutParams);
	    }
	}



	private static class BtnBrightness {
	    BtnBrightness(LayoutParams lp, float v) {
	        lp.buttonBrightness = v;
	    }
	}

	
	//Hook to allow testing of toggle mode private method
	public void hookToggleMode(){
		toggleMode();
	}
	
	//Used to customize the menu display
	//hacked from http://stackoverflow.com/questions/2944244/change-the-background-color-of-the-options-menu/5647743#5647743
	protected Boolean setMenuBackground(Menu menu){                     
	    Log.d("JoeDebug", "Enterting setMenuBackGround");
		getLayoutInflater().setFactory( new Factory() {  
	        public View onCreateView(String name, Context context, AttributeSet attrs) {
	            if ( name.equalsIgnoreCase( "com.android.internal.view.menu.IconMenuItemView" ) ) {
	                try { // Ask our inflater to create the view  
	                    LayoutInflater f = getLayoutInflater();  
	                    final View[] view = new View[1];
	                    try
	                    {
	                    	view[0] = f.createView( name, null, attrs );  
	                    }
	                    catch (InflateException e)
	                    {
	                    	hackAndroid23(name, attrs, f, view);
	                    }
	                    /* The background gets refreshed each time a new item is added the options menu.  
	                    * So each time Android applies the default background we need to set our own  
	                    * background. This is done using a thread giving the background change as runnable 
	                    * object */
	                    new Handler().post(new Runnable() {  
	                        public void run () {  
	                        	Log.d("JoeDebug", "Setting menu background and text color in run()");
	                            // sets the background color   
	                            view[0].setBackgroundResource(R.color.black);
	                            // sets the text color              
	                            ((TextView) view[0]).setTextColor(0xAAFF0000);
	                            // sets the text size              
	                            //((TextView) view[0]).setTextSize(14);
	                        }
	                    } );  
	                    return view[0];
	                }
	                catch ( InflateException e ) 
	                {
	                	if (e != null)
	                	{
		                	Log.e("JoeDebug", "JoeExceptionDump: " + e.getMessage());
		                	Log.e("JoeDebug", "JoeExceptionDump: " + e.getStackTrace().toString());
	                	}
	                }
	                catch ( ClassNotFoundException e ) 
	                {
	                	if (e != null)
	                	{
		                	Log.e("JoeDebug", "JoeExceptionDump: " + e.getMessage());
		                	Log.e("JoeDebug", "JoeExceptionDump: " + e.getStackTrace().toString());
	                	}
	                }
	            } 
	            return null;
	        }
	    }); 
		
		return super.onCreateOptionsMenu(menu);
	}
	
	//needed to make the above menu background method work
	//hacked from http://stackoverflow.com/questions/2944244/change-the-background-color-of-the-options-menu/5647743#5647743
	static void hackAndroid23(final String name,
	        final android.util.AttributeSet attrs, final LayoutInflater f,
	        final View[] view) 
	{
	    // mConstructorArgs[0] is only non-null during a running call to inflate()
	    // so we make a call to inflate() and inside that call our dully XmlPullParser get's called
	    // and inside that it will work to call "f.createView( name, null, attrs );"!
	    try 
	    {
	    	f.inflate(new XmlPullParser() 
	    	{
				public int next() throws XmlPullParserException, IOException 
				{
	                try 
	                {
	                    view[0] = (TextView) f.createView( name, null, attrs );
	                } 
	                catch (InflateException e) 
	                {
	                } 
	                catch (ClassNotFoundException e) 
	                {
	                }
	                throw new XmlPullParserException("exit");
				}

				//New XmlPullParser require us to implement all abstract methods, so here we go!
				public void defineEntityReplacementText(String entityName,
						String replacementText) throws XmlPullParserException {
					// TODO Auto-generated method stub
					
				}

				public int getAttributeCount() {
					// TODO Auto-generated method stub
					return 0;
				}

				public String getAttributeName(int index) {
					// TODO Auto-generated method stub
					return null;
				}

				public String getAttributeNamespace(int index) {
					// TODO Auto-generated method stub
					return null;
				}

				public String getAttributePrefix(int index) {
					// TODO Auto-generated method stub
					return null;
				}

				public String getAttributeType(int index) {
					// TODO Auto-generated method stub
					return null;
				}

				public String getAttributeValue(int index) {
					// TODO Auto-generated method stub
					return null;
				}

				public String getAttributeValue(String namespace, String name) {
					// TODO Auto-generated method stub
					return null;
				}

				public int getColumnNumber() {
					// TODO Auto-generated method stub
					return 0;
				}

				public int getDepth() {
					// TODO Auto-generated method stub
					return 0;
				}

				public int getEventType() throws XmlPullParserException {
					// TODO Auto-generated method stub
					return 0;
				}

				public boolean getFeature(String name) {
					// TODO Auto-generated method stub
					return false;
				}

				public String getInputEncoding() {
					// TODO Auto-generated method stub
					return null;
				}

				public int getLineNumber() {
					// TODO Auto-generated method stub
					return 0;
				}

				public String getName() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamespace() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamespace(String prefix) {
					// TODO Auto-generated method stub
					return null;
				}

				public int getNamespaceCount(int depth)
						throws XmlPullParserException {
					// TODO Auto-generated method stub
					return 0;
				}

				public String getNamespacePrefix(int pos)
						throws XmlPullParserException {
					// TODO Auto-generated method stub
					return null;
				}

				public String getNamespaceUri(int pos)
						throws XmlPullParserException {
					// TODO Auto-generated method stub
					return null;
				}

				public String getPositionDescription() {
					// TODO Auto-generated method stub
					return null;
				}

				public String getPrefix() {
					// TODO Auto-generated method stub
					return null;
				}

				public Object getProperty(String name) {
					// TODO Auto-generated method stub
					return null;
				}

				public String getText() {
					// TODO Auto-generated method stub
					return null;
				}

				public char[] getTextCharacters(int[] holderForStartAndLength) {
					// TODO Auto-generated method stub
					return null;
				}

				public boolean isAttributeDefault(int index) {
					// TODO Auto-generated method stub
					return false;
				}

				public boolean isEmptyElementTag()
						throws XmlPullParserException {
					// TODO Auto-generated method stub
					return false;
				}

				public boolean isWhitespace() throws XmlPullParserException {
					// TODO Auto-generated method stub
					return false;
				}

				public int nextTag() throws XmlPullParserException, IOException {
					// TODO Auto-generated method stub
					return 0;
				}

				public String nextText() throws XmlPullParserException,
						IOException {
					// TODO Auto-generated method stub
					return null;
				}

				public int nextToken() throws XmlPullParserException,
						IOException {
					// TODO Auto-generated method stub
					return 0;
				}

				public void require(int type, String namespace, String name)
						throws XmlPullParserException, IOException {
					// TODO Auto-generated method stub
					
				}

				public void setFeature(String name, boolean state)
						throws XmlPullParserException {
					// TODO Auto-generated method stub
					
				}

				public void setInput(Reader in) throws XmlPullParserException {
					// TODO Auto-generated method stub
					
				}

				public void setInput(InputStream inputStream,
						String inputEncoding) throws XmlPullParserException {
					// TODO Auto-generated method stub
					
				}

				public void setProperty(String name, Object value)
						throws XmlPullParserException {
					// TODO Auto-generated method stub
					
				}
	    	}, null, false);
	    } 
	    catch (InflateException e1) 
	    {
	        // "exit" ignored
	    }
	}
}
