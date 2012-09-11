function openBigDialog(cHeight,cWidth,vertPadding,horzPadding,minHeight,minWidth,cData,loadData){
	var win = $(window);
	var overlay = $("#big_dialog_overlay_back");
	var center  = $("#big_dialog_overlay_box");
	if(!overlay.is(':visible')){	
		overlay.css("opacity", .5).show();
		var middle = win.scrollTop() + (win.height() / 2);
		var centerHeight 	= 10;
		var centerWidth 	= 10;
		center.css({top: Math.max(0, middle - (centerHeight / 2)), width: centerWidth, height: centerHeight, marginLeft: -centerWidth/2}).show();
		center.show();	
		var l = win.scrollLeft(), w = win.width();
		center.css("left", l + (w / 2));	
		var dims = calcDialogDims(cHeight,cWidth,vertPadding,horzPadding,minHeight,minWidth);
		centerHeight = dims[0];
		centerWidth  = dims[1];
		var top = Math.max(0, middle - (centerHeight / 2));
		center.css({height: centerHeight, top: top, width: centerWidth, marginLeft: -centerWidth/2});
		if(loadData){center.html(cData);}
		if (window.PIE) {$('#big_dialog_overlay_box').each(function() {PIE.attach(this);});}
	}
}

function closeBigDialog(selectorsToHide){
	if (window.PIE) {$('#big_dialog_overlay_box').each(function() {PIE.detach(this);});}
	$(selectorsToHide).hide();
	$("#big_dialog_overlay_back").hide();
	$("#big_dialog_overlay_box").hide();		
}

function openSmallDialog(cHeight,cWidth,vertPadding,horzPadding,minHeight,minWidth,selectorsToHide,onCloseCallback,cData,loadData){
	var win = $(window);
	var overlay = $("#small_dialog_overlay_back");
	// When you click off small dialog, close window and execute callbacks
	overlay.on('click',function(){closeSmallDialog(selectorsToHide);onCloseCallback();});
	var center  = $("#small_dialog_overlay_box");
	//var content = $("#sdob_content");
	var horizontal = $('.sdob_horizontal');
	var vertical = $('.sdob_vertical');
	var borderPadding = 0;
	if(!overlay.is(':visible')){	
		overlay.css("opacity", .0).show();
		var middle = win.scrollTop() + (win.height() / 2);
		var centerHeight 	= 10;
		var centerWidth 	= 10;
		center.css({top: Math.max(0, middle - (centerHeight / 2)), width: centerWidth, height: centerHeight, marginLeft: -centerWidth/2}).show();
		center.show();	
		//content.show();
		var l = win.scrollLeft(), w = win.width();
		center.css("left", l + (w / 2));	
		var dims = calcDialogDims(cHeight+borderPadding,cWidth+borderPadding,vertPadding,horzPadding,minHeight,minWidth);
		centerHeight = dims[0];
		centerWidth  = dims[1];
		var top = Math.max(0, middle - (centerHeight / 2));
		center.css({height: centerHeight, top: top, width: centerWidth, marginLeft: -centerWidth/2, overflow: "hidden"});
		horizontal.css({width: (centerWidth-borderPadding)});
		vertical.css({height: (centerHeight-borderPadding)});

		//content.css({height: (centerHeight-borderPadding),  width: (centerWidth-borderPadding)});
		if(loadData){center.html(cData);}
		if (window.PIE) {$('#big_dialog_overlay_box').each(function() {PIE.attach(this);});}
	}
}

function closeSmallDialog(selectorsToHide){
	if (window.PIE) {$('#big_dialog_overlay_box').each(function() {PIE.detach(this);});}
	$(selectorsToHide).hide();
	$("#small_dialog_overlay_back").hide();
	$("#small_dialog_overlay_box").hide();		
}


function calcDialogDims(cHeight,cWidth,vertPadding,horzPadding,minHeight,minWidth){
	var win = $(window);
	var calcMaxHeight = win.height() - vertPadding;
	var calcMaxWidth  = win.width() - horzPadding;
	var centerHeight = Math.max(Math.min(cHeight,calcMaxHeight),minHeight);		
	var centerWidth = Math.max(Math.min(cWidth,calcMaxWidth),minWidth);
	return new Array(centerHeight,centerWidth);
}

function initOverlayAndDialogs(){
	var html = '<div id="big_dialog_overlay_back"></div><div id="big_dialog_overlay_box"></div>\
				<div id="small_dialog_overlay_back"></div><div id="small_dialog_overlay_box"></div>';
				
	$('body').append(html);
}

$(function(){
	// Append dialog boxes and overlays to body
	initOverlayAndDialogs();
});




