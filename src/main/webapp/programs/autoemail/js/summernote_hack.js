function reapply_textarea(id){
    var realTextboxParent = document.getElementById(id).nextSibling;
    var realContent;
    for (var i=0; i<realTextboxParent.childNodes.length; i++){
        var child = realTextboxParent.childNodes[i];
        if(child.className === 'note-editable'){
            document.getElementById(id).innerHTML = child.innerHTML;
            break;
        }
    }
    
    /*
    var jsfTextbox = document.getElementById(id);
    
    jsfTextbox.innerHTML = realContent; //For some weird reasons, this alone works.
    */
}
/*
$(document).ready(function () {
    var editors = document.getElementsByClassName('editor');
    for(var i=0; i<editors.length; i++) {
        var editor = editors[i];
        editor.addEventListener(
            "submit",reapply_textarea(editor.id));
    }
            
});*/