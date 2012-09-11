$(function(){
	var winHeight = $(window).height(),
		winWidth = $(window).width();
		
	var header = $('#header'),
		content = $('#content'),
		footer = $('#footer'),
		menuItems = $('.footer-menu-item',footer);		
		
	$(window).bind('resize',function(){
		var numMenuItems = menuItems.length,
			menuItemWidth = Math.floor(winWidth / 4) - 1;
			
		$.each(menuItems,function(i, elem){
			var item = $(elem);
			if(i==(numMenuItems-1)){
				item.css({'width':(menuItemWidth+1)+'px','borderRight':'0px'});
			}else{
				item.css({'width':menuItemWidth+'px'});
			}
		});
	});
	
	$(window).trigger('resize');


	// 1. Update content height to be device width - header - footer;
	
	
	
	// 2. Bind menu events
	$.each(menuItems,function(i, item){
		$(item).bind('click',function(){
			var button = $(this),
				myContent = $('#'+button.attr('data-content-id'));
			$('.footer-menu-item-clicked',footer).removeClass('footer-menu-item-clicked');
			$('.content-open',content).removeClass('content-open');
			button.addClass('footer-menu-item-clicked');
			myContent.addClass('content-open');
		});
		
	});
	
	
	// 3. 
});