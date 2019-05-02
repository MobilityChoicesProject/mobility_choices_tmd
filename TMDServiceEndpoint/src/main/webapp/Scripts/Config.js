/**
 * Created by Johannes on 09.08.2017.
 */
var lastSettings;




function testIfTokenIsValid(){

  setTimeout(function () {

    var token = getToken();

    var tok = {tokenToCheck: token};

    var data1 = JSON.stringify(tok);

    var url = getAppName()+"rest/User/isValidToken";
    $.ajax({
      type: "POST",
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      data: data1,
      url: url,
      success: function(result){
        if(result.tokenIsValid == true){

        }else{
          alert("Session expired")
          goToLoginPageAndDeleteToken();
        }
      },
      error:function (result)
      {
        alert("internal error");
      }
    });

  },100);





}

function showChangePassword(){
  testIfTokenIsValid();
  deactivateGisConfig();

  $( "#gisContainer" ).hide();
  // $( "#usernameId" ).val("");
  $( "#oldPasswordId" ).val("");
  $( "#newPasswordId1" ).val("");
  $( "#newPasswordId2" ).val("");

  $( "#container2" ).show();
  $( "#passwordContainer" ).show();
  $( "#configContainer" ).hide();

}

function showChangeParameters(){
  testIfTokenIsValid();
  deactivateGisConfig();
  $( "#gisContainer" ).hide();
  $( "#container2" ).show();
  $( "#passwordContainer" ).hide();
  $( "#configContainer" ).show();


  var token = getCookie("tokenValue");
  if(token  == null){
    goToLoginPageAndDeleteToken();
  }

  var url = getAppName()+"rest/ConfigService/currentGroups";
  $.ajax({
    type: "GET",
    beforeSend: function(request) {
      request.setRequestHeader("Authorization", "Token "+token);
    },
    contentType: "application/json; charset=utf-8",
    dataType: "json",
    url: url,
    success: function(result){
      handleConfigSettins(result);

    },
    error:function (result)
    {
      // handleError(result);
    }
  });

}

function showGisCacheConfig(){

  $( "#container2" ).show();
  $( "#passwordContainer" ).hide();
  $( "#configContainer" ).hide();
  $( "#gisContainer" ).show();

  activateGisConfig();

}



function handleConfigSettins(settings){

  var k = 0;
  lastSettings = [];
  $("#configItems").empty();

  var arrayLength = settings.length;

  for(var i = 0; i < arrayLength;i++){

    var group = settings[i];
    var name = group.name;
    var description = group.description;
    description = description.replace(/</g,"&lt;");
    description = description.replace(/>/g,"&gt;");
    description = description.replace(/\n/g,"<br />");

    var configs = group.configSettingList;
    var configSettingDiv = $('<div></div>')
    configSettingDiv.attr("data-role","collapsible");
    configSettingDiv.append("<h4>"+name+"</h4><p>"+description+"</p>");




    var configTable = $('<table></table>')

    configSettingDiv.append(configTable);

    var configSize = configs.length;
    for (var j = 0; j < configSize; j++) {
      var setting =  configs[j];
      var useDefaultButton = "useDefault('"+setting.key+"')";
      var tableRow = " <tr>  <td> <div class='tooltip'><label>"+setting.name+"</label> <span class='tooltiptext'>"+setting.description+"</span> </div><br> </td> <td> <input class='configInputClass' id='input_"+setting.key+"' type='number' step='0.001' value='"+setting.value+"' name='uname' required> </td> <td> <button type='submit' onclick='useLast(&quot;"+setting.key+"&quot;)' >Undo</button> </td> <td> <button type='submit' onclick='useDefault(&quot;"+setting.key+"&quot;)' >Default</button> </td> </tr>";
      configTable.append(tableRow);
      lastSettings[k++] = setting;
    }


    $("#configItems").append(configSettingDiv);
    $("#configItems").enhanceWithin();

  }

}

function useLast(key){
  var id= "#input_"+key;
  var arrayLength = lastSettings.length;
  for (var i = 0; i < arrayLength; i++) {
    var setting =  lastSettings[i];
    if(setting.key == key){
      var inputField =$(id);
      inputField.val(setting.value);
      break;
    }
  }
}

function useDefault(key){
  var token = getToken();

  var  url = getAppName()+"rest/ConfigService/getAllDefaultSettings";
  $.ajax({
    type: "POST",
    beforeSend: function(request) {
      request.setRequestHeader("Authorization", "Token "+token);
    },
    contentType: "application/json; charset=utf-8",
    url: url,
    success: function(result){
      useDefaultFor(result,key);
    },
    error:function (result)
    {

    }
  });
}

function useDefaultFor(results,key){
  var arrayLength = results.length;
  for (var i = 0; i < arrayLength; i++) {
    var setting = results[i];

    var keyFromSettings = setting.key;

    if(keyFromSettings == key){
      var id= "#input_"+setting.key;
      $(id).val(setting.value);
    }
  }
}

function useDefaultForAll(){
  var token = getToken();

  var url  = getAppName()+"rest/ConfigService/getAllDefaultSettings";
  $.ajax({
    type: "POST",
    beforeSend: function(request) {
      request.setRequestHeader("Authorization", "Token "+token);
    },
    contentType: "application/json; charset=utf-8",
    url: url,
    success: function(result){
      useDefaultForAllHandler(result);
    },
    error:function (result)
    {
      alert("internal error");
    }
  });
}

function useDefaultForAllHandler(result){
  var arrayLength = result.length;
  for (var i = 0; i < arrayLength; i++) {
    var setting = result[i];

    var key = setting.key;

    var id= "#input_"+setting.key;
    $(id).val(setting.value);
  }
}


function saveNewSettings(){

  var listOfObjects = [];
  var arrayLength = lastSettings.length;
  for (var i = 0; i < arrayLength; i++) {
    var setting =  lastSettings[i];
    var id= "#input_"+setting.key;

    var value =$(id).val();
    if(value == setting.value){
    }else{
      var newSetting = {key:setting.key,value: value};
      listOfObjects.push(newSetting)
    }
  }
  var data1 = JSON.stringify(listOfObjects);
  var token = getToken();

  var url  = getAppName()+"rest/ConfigService/updateConfigSettings";
  $.ajax({
    type: "POST",
    beforeSend: function(request) {
      request.setRequestHeader("Authorization", "Token "+token);
    },
    contentType: "application/json; charset=utf-8",
    dataType: "json",
    data:data1,
    url: url,
    success: function(result){
      if(result.status == "OK"){
        alert("Config changed");
        $("#loginError").hide();

      }else{
        $("#loginError").show();
      }

    },
    error:function (result)
    {

    }
  });




}

function changePwd(){

  var oldPwd = $( "#oldPasswordId" ).val();
  var newPWD1 =  $( "#newPasswordId1" ).val();
  var newPWD2 =   $( "#newPasswordId2" ).val();

  if(newPWD1 != newPWD2){
    alert("New Passwords have to be the equal");
  }else{

    var passwordChangeRequestObj = {oldPassword:oldPwd.split(''),newPassword: newPWD1.split('')};
    var jsonFile = JSON.stringify(passwordChangeRequestObj);
    var token = getToken();


    var url  = getAppName()+"rest/User/changePassword";
    $.ajax({
      type: "POST",
      beforeSend: function(request) {
        request.setRequestHeader("Authorization", "Token "+token);
      },
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      data:jsonFile,
      url: url,
      success: function(result){
        alert("Password changed");

      },
      error:function (result)
      {
        alert("Password changing failed");

      }
    })




  }
}