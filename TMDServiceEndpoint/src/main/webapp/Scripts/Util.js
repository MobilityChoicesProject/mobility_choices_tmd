/**
 * Created by Johannes on 09.08.2017.
 */
function getCookie(cname) {
  var name = cname + "=";
  var decodedCookie = decodeURIComponent(document.cookie);
  var ca = decodedCookie.split(';');
  for(var i = 0; i <ca.length; i++) {
    var c = ca[i];
    while (c.charAt(0) == ' ') {
      c = c.substring(1);
    }
    if (c.indexOf(name) == 0) {
      return c.substring(name.length, c.length);
    }
  }
  return "";
}

function handleError(error){

  goToLoginPageAndDeleteToken();

}

function goToLoginPageAndDeleteToken(){
  var url = getAppName()+"Login.html";

  document.cookie = "tokenValue="+";expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/";
  window.location.href = url;
}

function getToken() {
  var url = getAppName()+"Login.html";

  var token = getCookie("tokenValue");
  if(token  == null){
    window.location.href = url;
  }
  return token;
}

function logout(){

  var url = getAppName()+"Login.html";
  var tokenStr = getCookie("tokenValue");
  if(tokenStr  == null){
    window.location.href = url;
  }

  var tok = {tokenToCheck: tokenStr};
  var data1 = JSON.stringify(tok);

  var url = getAppName()+"rest/User/logout";
  $.ajax({
    type: "POST",
    contentType: "application/json; charset=utf-8",
    dataType: "json",
    data: data1,
    url: url,
    success: function(result){
      goToLoginPageAndDeleteToken();
    },
    error:function (result)
    {
      goToLoginPageAndDeleteToken();
    }
  });



}

function getAppName(){

  var applicationName = window.location.pathname.split('/')[1];
  return "/"+applicationName+"/";

}