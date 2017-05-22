/* ----- cookie_functions.js ----- */
function createCookie(name,value,days) {
    if (days) {
        var date = new Date();
        date.setTime(date.getTime()+(days*24*60*60*1000));
        var expires = "; expires="+date.toGMTString();
    } else {
        expires = "";
    }
    document.cookie = name+"="+escape(value)+expires+"; path=/;";
};

function readCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') {
            c = c.substring(1,c.length);
        }
        if (c.indexOf(nameEQ) == 0) {
            return unescape(c.substring(nameEQ.length,c.length));
        }
    }
    return null;
};


/* ----- nodeutilities.js ----- */

function hasClassName(node, class_name) {
    return new RegExp('\\b'+class_name+'\\b').test(node.className);
};

function addClassName(node, class_name) {
    if (!node.className) {
        node.className = class_name;
    } else if (!hasClassName(node, class_name)) {
        var className = node.className+" "+class_name;
        // cleanup
        node.className = className.split(/\s+/).join(' ');
    }
};

function removeClassName(node, class_name) {
    var className = node.className;
    if (className) {
        // remove
        className = className.replace(new RegExp('\\b'+class_name+'\\b'), '');
        // cleanup
        className = className.replace(/\s+/g, ' ');
        node.className = className.replace(/\s+$/g, '');
    }
};

function replaceClassName(node, old_class, new_class, ignore_missing) {
    if (ignore_missing && !hasClassName(node, old_class)) {
        addClassName(node, new_class);
    } else {
        var className = node.className;
        if (className) {
            // replace
            className = className.replace(new RegExp('\\b'+old_class+'\\b'), new_class);
            // cleanup
            className = className.replace(/\s+/g, ' ');
            node.className = className.replace(/\s+$/g, '');
        }
    }
};

/* ----- fullscreenmode.js ----- */
function toggleFullScreenMode() {
    var body = document.body;
    if (hasClassName(body, 'fullscreen')) {
        // unset cookie
        removeClassName(body, 'fullscreen');
        createCookie('fullscreenMode', '');
    } else {
        // set cookie
        addClassName(body, 'fullscreen');
        createCookie('fullscreenMode', '1');
    }
};

function fullscreenModeLoad() {
    // based on cookie
    if (readCookie('fullscreenMode') == '1') {
        var body = document.body;
        addClassName(body, 'fullscreen');
    }
};

function startList() {
    if (document.all && document.getElementById) {
        navRoot = document.getElementById("dropdowns");
        for (i=0; i<navRoot.childNodes.length; i++) {
            node = navRoot.childNodes[i];
            if (node.nodeName=="LI") {
                node.onmouseover=function() {
                    addClassName(this, 'over');
                }
                node.onmouseout=function() {
                    removeClassName(this, 'over');
                }
            }
        }
    }
}

/* Use attachEvent for IE */
function addEvent(obj, evType, fn){
 if (obj.addEventListener){
   obj.addEventListener(evType, fn, false);
   return true;
 } else if (obj.attachEvent){
   var r = obj.attachEvent("on"+evType, fn);
   return r;
 } else {
   return false;
 }
}

addEvent(window,'load',startList);
addEvent(window,'load',fullscreenModeLoad);

