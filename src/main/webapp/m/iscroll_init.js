$('#wrapper').ready(function(){
	// Put outside onready load to avoid issues
	// Setup iscroll4 scrolling of claims
	var myScroll,
		pullDownEl, pullDownOffset,
		pullUpEl, pullUpOffset,
		generatedCount = 0,
		testAmt = new Array("$45","$25","$95","$30","$245"),
		testStr = new Array("ALRGY TST","DR VST","WLNS CHCKP","EAR ACH/SHT","XRY / SET");

	function pullDownAction () {
		setTimeout(function () {	// <-- Simulate network congestion, remove setTimeout from production!
			var el, li, i, link, curGen;
			el = document.getElementById('thelist');

			for (i=0; i<3; i++) {
				curGen = ++generatedCount;
				generatedCount = generatedCount > 28 ? 1 : generatedCount;
				curGen = curGen > 28 ? 1 : curGen;
				curGen = curGen < 10 ? '0' + curGen : curGen;
				li = document.createElement('li');
				link = document.createElement('a');
				link.innerHTML = '02-'+ curGen +'-12 <STRONG>'+testAmt[Math.floor(Math.random()*5)]+'</STRONG> '+testStr[Math.floor(Math.random()*5)];
				link.href = '#democlaim';
				li.insertBefore(link, li.childNodes[0]);
				el.insertBefore(li, el.childNodes[0]);
			}
			
			myScroll.refresh();		// Remember to refresh when contents are loaded (ie: on ajax completion)
		}, 1000);	// <-- Simulate network congestion, remove setTimeout from production!
	}

	function pullUpAction () {
		setTimeout(function () {	// <-- Simulate network congestion, remove setTimeout from production!
			var el, li, i, link, curGen;
			el = document.getElementById('thelist');

			for (i=0; i<3; i++) {
				curGen = ++generatedCount;
				generatedCount = generatedCount > 28 ? 1 : generatedCount;
				curGen = curGen > 28 ? 1 : curGen;
				curGen = curGen < 10 ? '0' + curGen : curGen;
				li = document.createElement('li');
				link = document.createElement('a');
				link.innerHTML = '04-'+ curGen +'-11 | <STRONG>'+testAmt[Math.floor(Math.random()*5)]+'</STRONG> '+testStr[Math.floor(Math.random()*5)];
				link.href = '#democlaim';
				li.insertBefore(link, li.childNodes[0]);
				el.appendChild(li);
			}
			
			myScroll.refresh();		// Remember to refresh when contents are loaded (ie: on ajax completion)
		}, 1000);	// <-- Simulate network congestion, remove setTimeout from production!
	}

	function loaded() {
		pullDownEl = document.getElementById('pullDown');
		pullDownOffset = pullDownEl.offsetHeight;
		pullUpEl = document.getElementById('pullUp');	
		pullUpOffset = pullUpEl.offsetHeight;
		
		myScroll = new iScroll('wrapper', {
			useTransition: true,
			topOffset: 51,
			onRefresh: function () {
				if (pullDownEl.className.match('loading')) {
					pullDownEl.className = '';
					pullDownEl.querySelector('.pullDownLabel').innerHTML = 'Pull down to refresh...';
				} else if (pullUpEl.className.match('loading')) {
					pullUpEl.className = '';
					pullUpEl.querySelector('.pullUpLabel').innerHTML = 'Pull up to load more...';
				}
			},
			onScrollMove: function () {
				if (this.y > 5 && !pullDownEl.className.match('flip')) {
					pullDownEl.className = 'flip';
					pullDownEl.querySelector('.pullDownLabel').innerHTML = 'Release to refresh...';
					this.minScrollY = 0;
				} else if (this.y < 5 && pullDownEl.className.match('flip')) {
					pullDownEl.className = '';
					pullDownEl.querySelector('.pullDownLabel').innerHTML = 'Pull down to refresh...';
					this.minScrollY = -pullDownOffset;
				} else if (this.y < (this.maxScrollY - 5) && !pullUpEl.className.match('flip')) {
					pullUpEl.className = 'flip';
					pullUpEl.querySelector('.pullUpLabel').innerHTML = 'Release to refresh...';
					this.maxScrollY = this.maxScrollY;
				} else if (this.y > (this.maxScrollY + 5) && pullUpEl.className.match('flip')) {
					pullUpEl.className = '';
					pullUpEl.querySelector('.pullUpLabel').innerHTML = 'Pull up to load more...';
					this.maxScrollY = pullUpOffset;
				}
			},
			onScrollEnd: function () {
				if (pullDownEl.className.match('flip')) {
					pullDownEl.className = 'loading';
					pullDownEl.querySelector('.pullDownLabel').innerHTML = 'Loading...';				
					pullDownAction();	// Execute custom function (ajax call?)
				} else if (pullUpEl.className.match('flip')) {
					pullUpEl.className = 'loading';
					pullUpEl.querySelector('.pullUpLabel').innerHTML = 'Loading...';				
					pullUpAction();	// Execute custom function (ajax call?)
				}
			}
		});
		
		setTimeout(function () { document.getElementById('wrapper').style.left = '0'; }, 800);
		myScroll.refresh();	
	}

    document.addEventListener('DOMContentLoaded', loaded, true);
	document.addEventListener('touchmove', function (e) { e.preventDefault(); }, false);

	// Call init method
	loaded();
	
	
	
});
