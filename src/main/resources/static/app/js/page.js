/*左侧菜单响应式*/
function Huiasidedisplay(){
	if($(window).width()>=768){
		$(".Hui-aside").show()
	} 
}


$(function(){
	Huiasidedisplay();
	var resizeID;
	$(window).resize(function(){
		clearTimeout(resizeID);
		resizeID = setTimeout(function(){
			Huiasidedisplay();
		},500);
	});
	
	$(".nav-toggle").click(function(){
		$(".Hui-aside").slideToggle();
	});
	$(".Hui-aside").on("click",".menu_dropdown dd li a",function(){
		if($(window).width()<768){
			$(".Hui-aside").slideToggle();
		}
	});
	
	/*左侧菜单*/
	$(".Hui-aside").Huifold({
		titCell:'.menu_dropdown dl dt',
		mainCell:'.menu_dropdown dl dd',
	});	
		

});



/* =======================================================================
 * jQuery.Huisidenav.js 左侧菜单-隐藏显示
 * ======================================================================== */
function displaynavbar(obj){
    if($(obj).hasClass("open")){
        $(obj).removeClass("open");
        $("body").removeClass("big-page");
    } else {
        $(obj).addClass("open");
        $("body").addClass("big-page");
    }
}

/* =======================================================================
 * jQuery.Huihover.js v2.0 Huihover
 * http://www.h-ui.net/
 * Created & Modified by guojunhui
 * Date modified 2017.05.05
 *
 * Copyright 2017 北京颖杰联创科技有限公司 All rights reserved.
 * Licensed under MIT license.
 * http://opensource.org/licenses/MIT
 * ========================================================================*/
!function($) {
    $.fn.Huihover = function(options){
        var defaults = {
            className:"hover",
        }
        var options = $.extend(defaults, options);
        this.each(function(){
            var that = $(this);
            that.hover(function() {
                    that.addClass(options.className);
                },
                function() {
                    that.removeClass(options.className);
                });
        });
    }
} (window.jQuery);

/* =======================================================================
 * jQuery.Huifocusblur.js v2.0 得到失去焦点
 * http://www.h-ui.net/
 * Created & Modified by guojunhui
 * Date modified 2017.05.09
 *
 * Copyright 2017 北京颖杰联创科技有限公司 All rights reserved.
 * Licensed under MIT license.
 * http://opensource.org/licenses/MIT
 * ========================================================================*/
!function($) {
    $.fn.Huifocusblur = function(options){
        var defaults = {
            className:"focus",
        }
        var options = $.extend(defaults, options);
        this.each(function(){
            var that = $(this);
            that.focus(function() {
                that.addClass(options.className).removeClass("inputError");
            });
            that.blur(function() {
                that.removeClass(options.className);
            });
        });
    }
} (window.jQuery);

/* =======================================================================
 * jQuery.Huiselect.js 选择
 * ========================================================================*/
!function($) {
    $.Huiselect = function(divselectid, inputselectid) {
        var inputselect = $(inputselectid);
        $(divselectid + " cite").click(function() {
            var ul = $(divselectid + " ul");
            ul.slideToggle();
        });
        $(divselectid + " ul li a").click(function() {
            var txt = $(this).text();
            $(divselectid + " cite").html(txt);
            var value = $(this).attr("selectid");
            inputselect.val(value);
            $(divselectid + " ul").hide();
        });
        $(document).click(function() {
            $(divselectid + " ul").hide();
        });
    };
} (window.jQuery);

/* =======================================================================
 * jQuery.Huitab.js v2.0 选项卡
 * http://www.h-ui.net/
 * Created & Modified by guojunhui
 * Date modified 2017.05.05
 *
 * Copyright 2017 北京颖杰联创科技有限公司 All rights reserved.
 * Licensed under MIT license.
 * http://opensource.org/licenses/MIT
 * ========================================================================*/
!function($) {
    $.fn.Huitab = function(options){
        var defaults = {
            tabBar:'.tabBar span',
            tabCon:".tabCon",
            className:"current",
            tabEvent:"click",
            index:0,
        }
        var options = $.extend(defaults, options);
        this.each(function(){
            var that = $(this);
            that.find(options.tabBar).removeClass(options.className);
            that.find(options.tabBar).eq(options.index).addClass(options.className);
            that.find(options.tabCon).hide();
            that.find(options.tabCon).eq(options.index).show();

            that.find(options.tabBar).on(options.tabEvent,function(){
                that.find(options.tabBar).removeClass(options.className);
                $(this).addClass(options.className);
                var index = that.find(options.tabBar).index(this);
                that.find(options.tabCon).hide();
                that.find(options.tabCon).eq(index).show();
            });
        });
    }
} (window.jQuery);

/* =======================================================================
 * jQuery.Huifold.js v2.0 折叠
 * http://www.h-ui.net/
 * Created & Modified by guojunhui
 * Date modified 2017.05.05
 *
 * Copyright 2017 北京颖杰联创科技有限公司 All rights reserved.
 * Licensed under MIT license.
 * http://opensource.org/licenses/MIT
 * ========================================================================*/
!function($) {
    $.fn.Huifold = function(options){
        var defaults = {
            titCell:'.item .Huifold-header',
            mainCell:'.item .Huifold-body',
            type:1,//1	只打开一个，可以全部关闭;2	必须有一个打开;3	可打开多个
            trigger:'click',
            className:"selected",
            speed:'first',
        }
        var options = $.extend(defaults, options);
        this.each(function(){
            var that = $(this);
            that.find(options.titCell).on(options.trigger,function(){
                if ($(this).next().is(":visible")) {
                    if (options.type == 2) {
                        return false;
                    } else {
                        $(this).next().slideUp(options.speed).end().removeClass(options.className);
                        if ($(this).find("b")) {
                            $(this).find("b").html("+");
                        }
                    }
                }else {
                    if (options.type == 3) {
                        $(this).next().slideDown(options.speed).end().addClass(options.className);
                        if ($(this).find("b")) {
                            $(this).find("b").html("-");
                        }
                    } else {
                        that.find(options.mainCell).slideUp(options.speed);
                        that.find(options.titCell).removeClass(options.className);
                        if (that.find(options.titCell).find("b")) {
                            that.find(options.titCell).find("b").html("+");
                        }
                        $(this).next().slideDown(options.speed).end().addClass(options.className);
                        if ($(this).find("b")) {
                            $(this).find("b").html("-");
                        }
                    }
                }
            });

        });
    }
} (window.jQuery);
