
var loginButton;
var userInput;
var passwordInput;
var loginError;

function initLogin(){
  $( "#loginButton" ).click(function () {
    login();
  });

  userInput = $( "#userInput" );
  passwordInput = $( "#passwordInput" );
  loginError=$( "#loginError" );
}

function login(){

  var userInputVal =userInput.val();
  var passwordInputVal =passwordInput.val();
  var passwordChars =passwordInputVal.split('');

  var loginRequest = {username: userInputVal,password:passwordChars};
  var data1 = JSON.stringify(loginRequest);
  var url = getAppName()+"rest/User/login";
  $.ajax({
    type: "POST",
    contentType: "application/json; charset=utf-8",
    dataType: "json",
    data: data1,
    url: url,
    success: function(result){
      handleSuccesfulLogin(result);
    },
    error:function (result)
    {
      loginError.show();
    }
  });

}

function handleSuccesfulLogin(token){

  var tokenValue = token.token;

  document.cookie = "tokenValue="+tokenValue+"; path=/";

  var applicationName = window.location.pathname.split('/')[1];
  var path = "/"+applicationName+"/Main.html";
  window.location.href = path;

}

