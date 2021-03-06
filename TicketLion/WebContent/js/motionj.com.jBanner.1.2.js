

(function($){
	$.fn.jBanner = function(opt){
		opt = $.extend({
			width : 0,
			height: 0,
			speed : 1000,
			delay : 4000,
			paging: true,
			space: 1,
			src: 'img'
		}, opt || {});

		return this.each(function(){
			// init
			var elt = $(this),
				items = elt.find('li'),
				pause = false,
				no = 1;

			var getPaging = function(){
				var str = '',
					onoff;
				str += '<p>';
				for(var i=0;i<items.length;i++){
					onoff = (i === 0 ? 'on' : 'off');
					str += '<img src="'+opt.src+'/'+ (i + 1) +'_'+onoff+'.png" alt="" />';
				}
				str += '</p>';
				return str;
			};

			if(opt.paging){
				elt.append(getPaging());
			};

			//CSS & class Setup
			elt.css({position : 'relative', overflow : 'hidden'});
			elt.find('img').css({border:0});
			elt.find('ul').css({position : 'relative'});
			elt.find('li').css({ top:0,left:0, listStyle: 'none'});	
//			elt.find('p').css({position : 'absolute',   
//								bottom:5, width: 100,
//								left:'48.5%', 'z-index' : 1, 
//								textalign : 'center', 
//								margin:0, 
//								lineHeight:0});
			
			elt.find('p').css({position : 'absolute',   
				bottom:5, width: 100,
				left:'45%', 'z-index' : 1, 
				textalign : 'center', 
				margin:0, 
				lineHeight:0});
			
			elt.find('p img').css({'cursor':'pointer',  margin:'0 '+opt.space+'px'});
			elt.find('ul li:not(:first)').hide();
			elt.find('ul li:first').addClass('on');
			elt.find('p img:first').addClass('on');

			// Action
			var chaingeImageName = function(obj, isOn){
				if(isOn){
					obj.attr('src', obj.attr('src').replace('off.png', 'on.png'));
				}else{ 
					obj.attr('src', obj.attr('src').replace('on.png', 'off.png'));
				}
			};

			var act = function(num, isOver){
				if(!elt.find('ul li:eq(' + num + ')').hasClass('on')){
					if(opt.paging){
						chaingeImageName(elt.find('p img.on'), false);
						elt.find('p img.on').removeClass('on');
						elt.find('p img:eq(' + num + ')').addClass('on');
						chaingeImageName(elt.find('p img:eq(' + num + ')'), true);
					};

					if(isOver){
						elt.find('ul li').hide().removeClass('on');
						elt.find('ul li:eq(' + num + ')').addClass('on').stop().fadeIn('fast');
					}else{
						elt.find('ul li.on').fadeOut(opt.speed).removeClass('on');
						elt.find('ul li:eq(' + num + ')').fadeIn(opt.speed).addClass('on');
					};

					no = (num >= items.length - 1 ? 0 : num + 1);
				}
			};

			// Controllers
			elt.on('mouseover', function(){
				pause = true;
			});
			elt.on('mouseleave', function(){
				pause = false;
			});
			elt.find('p img').each(function(i){
				$(this).on('mouseover', function(){
					act(i, true);
				});
			});

			setInterval(function(){ if(pause == false) act(no); }, opt.delay);
		});
	}
})(jQuery);