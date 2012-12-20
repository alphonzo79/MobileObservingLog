//*************************************************************
//Photoshop script to process star chart images that have been
//printed from Skycharts/Cartes de Ciel. Charts should be printed
//from a 90 degree FOV, black/white lines, centered on the target
//object, with no grid lines, no planets, no asteroids, no comets
//and no outlines on deep sky objects. Mag settings may change,
//but I think somewhere between 5.5 and 6.5 is good. Also, there
//should be no finder targets on the chart (we will create those
//here. 
//
//This script will ask for a source directory and process each 
//pdf image in that directory. It will open it with the given
//settings, crop it, add the finder targets, save a version for
//normal mode, change the color scheme and save a version for 
//use in night mode. All of the images will be saved in an output
//directory specified by the user.
//
//To use, move this to C:\Program Files\Adobe\Adobe Photoshop CS2\Presets\Scripts
//
//Joe Rowley -- September 2011
//**************************************************************



// enable double clicking from the Macintosh Finder or the Windows Explorer
#target photoshop;

// in case we double clicked the file
app.bringToFront();

// Set the ruler units to pixels
var originalRulerUnits = app.preferences.rulerUnits;
app.preferences.rulerUnits = Units.PIXELS;
//app.displayDialogs = DialogModes.NO;

// Set the Open folder and compile the file list
var inputFolder = Folder.selectDialog("Please select the input folder");
var fileList = inputFolder.getFiles("*.pdf");

//Destination Folder
var destinationFolder = Folder.selectDialog("Please select the destination folder");
var outfolderNormal = destinationFolder + "/normal";
if (outfolderNormal.exists == false) outfolder.create();
var outfolderNight = destinationFolder + "/night";
if (outfolderNight.exists == false) outfolder.create();

//Import the finder targets
	var telradRefFolder = Folder.selectDialog("Please select the location of the telrad targets file");
	var telradFileRef = new File(decodeURI(telradRefFolder) + "/TelradTarget.psd");
    var telradDocRef = app.open(telradFileRef);
    
    var telradLayer = telradDocRef.artLayers.getByName("Telrad");
    var copiedLayer = telradLayer.copy();
    
    telradDocRef.close(SaveOptions.DONOTSAVECHANGES);

// open files
for (var a in fileList){

	// creat PDF option object
	var pdfOpenOptions = new PDFOpenOptions;
	pdfOpenOptions.antiAlias = true;
	pdfOpenOptions.height = 1275;
	pdfOpenOptions.width = 1650;
	pdfOpenOptions.mode = OpenDocumentMode.RGB;
	pdfOpenOptions.bitsPerChannel = BitsPerChannelType.EIGHT;
	pdfOpenOptions.resolution = 150;
	pdfOpenOptions.page = 1;
	pdfOpenOptions.constrainProportions = true;

	open(fileList[a], pdfOpenOptions);

	// crop the image
	bounds = new Array(324, 298, 1324, 1008);
	app.activeDocument.crop(bounds);
	bounds = null;
	
	/*Moved to outside the loop
		
	//Import the finder targets
	var newLayer = app.activeDocument.artLayers.add();
    newLayer.name = "TelradLayer";
    app.activeDocument.activeLayer = newLayer;
    
    var telradFileRef = new File("C://Documents and Settings//Joe//My Documents//AndroidProjects//ObservingLog//NonCodeResources//StarChartResources//TelradTarget.psd");
    var telradDocRef = app.open(telradFileRef);
    
    var telradLayer = telradDocRef.artLayers.getByName("Telrad");
    var copiedLayer = telradLayer.copy();
    
    telradDocRef.close(SaveOptions.DONOTSAVECHANGES);

*/
	var newLayer = app.activeDocument.artLayers.add();
    newLayer.name = "TelradLayer";
    app.activeDocument.activeLayer = newLayer;
    
    
    app.activeDocument.paste();
    
    //Flatten the image
    app.activeDocument.flatten();
    
    // Save the images in the new folder
	app.displayDialogs = DialogModes.NO;
	var Name = app.activeDocument.name;
	var saveFile = new File(decodeURI(outfolderNormal) + "/" + Name + ".gif");
	
	var exportOptions = new ExportOptionsSaveForWeb();
	exportOptions.colors = 8;
	exportOptions.format = SaveDocumentType.COMPUSERVEGIF;
	
	app.activeDocument.exportDocument(saveFile, ExportType.SAVEFORWEB, exportOptions);
	
	//Make it black and red
	app.activeDocument.activeLayer.invert();
	var redLayer = app.activeDocument.artLayers.add();
    redLayer.name = "RedLayer";
    app.activeDocument.activeLayer = redLayer;
	
	var fillColor = new SolidColor();
	fillColor.rgb.red = 255;
	fillColor.rgb.green = 0;
	fillColor.rgb.blue = 0;
	
	var selRef = app.activeDocument.selection;
	var selBounds = Array(Array(0, 0), Array(app.activeDocument.width, 0), Array(app.activeDocument.width, app.activeDocument.height), Array(0, app.activeDocument.height));
	selRef.select(selBounds);	
	selRef.fill(fillColor, ColorBlendMode.NORMAL, 100, false);
	redLayer.blendMode = BlendMode.MULTIPLY;
    
    //Flatten the image
    app.activeDocument.flatten();
    
    // Save the images in the new folder
	app.displayDialogs = DialogModes.NO;
	var Name = app.activeDocument.name;
	var saveFile = new File(decodeURI(outfolderNight) + "/" + Name + ".gif");
	
	var exportOptions = new ExportOptionsSaveForWeb();
	exportOptions.colors = 8;
	exportOptions.format = SaveDocumentType.COMPUSERVEGIF;
	
	app.activeDocument.exportDocument(saveFile, ExportType.SAVEFORWEB, exportOptions);
	
	app.activeDocument.close(SaveOptions.DONOTSAVECHANGES);	
}

app.preferences.rulerUnits = originalRulerUnits;