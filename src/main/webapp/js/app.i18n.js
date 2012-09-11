$(function() {
	var lang = $('#i18n-lang').val();
	$.i18n.properties({
		name:'cloudsuite', 
		path:'i18n/', 
		mode:'map',
		language:lang
	});
});