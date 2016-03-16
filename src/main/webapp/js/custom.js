var editor = ace.edit("editor");
editor.setTheme("ace/theme/monokai");

$("#run").bind("click", function(){
	$("#run").prop("disabled",true);
	$("#results").hide();
	$.ajax({
		type: "POST",
		url: "execute",
		data: {"code" : editor.getValue(),
			   "ast"  : isFlagEnabled("ast"),
			   "noout": isFlagEnabled("noout"),
			   "st"   : isFlagEnabled("st"),
			   "l"    : isFlagEnabled("l")}		
	}).done(function(reply) {  
		$("#results .well").text(reply);
		var result = $("#results .well").html()
					.replace(new RegExp('\n', 'g'), "<br>");
		$("#results .well").html(result);
		$("#results").show();
		$("#run").prop("disabled",false);
	}).error(function(reply){
		console.log(reply);
		alert("Sorry, Something went wrong, check logs.");
		$("#run").prop("disabled",false);
	});	
});
$("#tests").bind("change", function(e){
	$.ajax({
		type: "GET",
		url: "tests/"+this.value
	}).done(function(reply) {  
		editor.setValue(reply);
	}).error(function(reply){
		console.log(reply);
		alert("Sorry, Something went wrong, check logs.");		
	});	
});

$("#themes").bind("change", function(e){
	editor.setTheme("ace/theme/"+this.value);
});

function isFlagEnabled(flagName){
	return $("[name="+ flagName+"]").parent().hasClass("active");
}
