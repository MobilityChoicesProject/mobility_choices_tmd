
var gisDataProgressbar;
var isUpdating  = false;

function requestServerForStatus(){

  var token = getToken();
  var  url = getAppName()+"rest/Gis/status";
  $.ajax({
    type: "Get",
    beforeSend: function(request) {
      request.setRequestHeader("Authorization", "Token "+token);
    },
    contentType: "application/json; charset=utf-8",
    url: url,
    success: function(result){

      updateGisStatus(result);
    },
    error:function (result)
    {
      handleError();
    }
  });
}
function requestCurrentUpdateStatus(){
  var token = getToken();
  var  url = getAppName()+"rest/Gis/getCurrentStatus";
  $.ajax({
    type: "Get",
    beforeSend: function(request) {
      request.setRequestHeader("Authorization", "Token "+token);
    },
    contentType: "application/json; charset=utf-8",
    url: url,
    success: function(result){

      updateCurrentGisStatus(result);
    },
    error:function (result)
    {

    }
  });
}

function updateCurrentGisStatus(result){

  var updating = result.updating;
  if (updating) {
    var percentage = result.updatedTiles / result.allTiles * 100;
    setProgressBar(percentage);
    changeUpdatingStatus(true);
  } else {
    setProgressBar(0);
    changeUpdatingStatus(false);
  }

}

function changeUpdatingStatus(isActivelyUpating){
  if(isActivelyUpating != isUpdating){
    isUpdating = isActivelyUpating;
    if(isUpdating){

      $("#gisConfig_startButtonId").hide();
      $("#gisConfig_startButtonId_Disabled").show();

      $("#gisConfig_cancelButtonId").show();
      $("#gisConfig_cancelButtonId_Disabled").hide();


    }else{
      $("#gisConfig_startButtonId").show();
      $("#gisConfig_startButtonId_Disabled").hide();

      $("#gisConfig_cancelButtonId").hide();
      $("#gisConfig_cancelButtonId_Disabled").show();
    }

  }


}

function getFormattedString(timestamp1){
  var formattedStr = pad(timestamp1.date.day,2)+"."+pad(timestamp1.date.month,2)+"."+pad(timestamp1.date.year,2)+" "+pad(timestamp1.time.hour,2)+":"+pad(timestamp1.time.minute,2)+":"+pad(timestamp1.time.second,2);
  return formattedStr;


}

function updateGisStatus(gisStatus){


  $("#gisConfigTable").html("");
  var thead = "   <thead>\n"
      + "  <td>\n"
      + "    Date\n"
      + "  </td>\n"
      + "  <td>\n"
      + "    South Lat.\n"
      + "  </td>\n"
      + "  <td>\n"
      + "    North Lat.\n"
      + "  </td>\n"
      + "  <td>\n"
      + "    West Lng.\n"
      + "  </td>\n"
      + "  <td>\n"
      + "    East Lng.\n"
      + "  </td>\n"
      + "  <td>\n"
      + "    Updated\n"
      + "  </td>\n"
      + "  <td>\n"
      + "    Status\n"
      + "  </td>\n"
      + "  </thead>";

  $("#gisConfigTable").append(thead);

  var arrayLength = gisStatus.length;
  for (var i = 0; i < arrayLength; i++) {
    var gisStati = gisStatus[i];

    var southLatitude = gisStati.southLatitude.toFixed(3);
    var northLatitude = gisStati.northLatitude.toFixed(3);
    var westLongitude = gisStati.westLongitude.toFixed(3);
    var eastLongitude = gisStati.eastLongitude.toFixed(3);
    var status = gisStati.Status;
    var numberOfTiles = gisStati.numberOfTiles;
    var numberOfUpdatedTiles = gisStati.numberOfUpdatedTiles;

    var formattedStr =getFormattedString(gisStati.timestamp);

    if(status == "running" && !isUpdating){
      status ='<button id="gisConfig_resumeButtonId" class="resumeButton" type="submit" onclick="resumeGisCacheUpdate()" class=" ui-btn ui-shadow ui-corner-all">Resume</button>';
    }
    var updatedStr = gisStati.numberOfUpdatedTiles+"/"+gisStati.numberOfTiles;

    var tableRow = " <tr>  <td>"+ formattedStr+"</td> <td>"+southLatitude+"</td> <td>"+northLatitude+" </td> <td>"+westLongitude+" </td>  <td>"+eastLongitude+" </td><td>"+updatedStr+"</td><td>"+status+"</td></tr>";
    $("#gisConfigTable").append(tableRow);

  }

}


function startGisCacheUpdate (){
  var token = getToken();
  var  url = getAppName()+"rest/Gis/start";

  $.ajax({
    type: "Get",
    beforeSend: function(request) {
      request.setRequestHeader("Authorization", "Token "+token);
    },
    contentType: "application/json; charset=utf-8",
    url: url,
    success: function(result){

    },
    error:function (result)
    {
      handleError();
    }
  });

  refreshGisStatus();

}

function stopGisCacheUpdate (){
  var token = getToken();
  var  url = getAppName()+"rest/Gis/stop";

  $.ajax({
    type: "Get",
    beforeSend: function(request) {
      request.setRequestHeader("Authorization", "Token "+token);
    },
    contentType: "application/json; charset=utf-8",
    url: url,
    success: function(result){

    },
    error:function (result)
    {
      handleError();
    }
  });

  refreshGisStatus();

}

function resumeGisCacheUpdate(){
  var token = getToken();
  var  url = getAppName()+"rest/Gis/resume";

  $.ajax({
    type: "Get",
    beforeSend: function(request) {
      request.setRequestHeader("Authorization", "Token "+token);
    },
    contentType: "application/json; charset=utf-8",
    url: url,
    success: function(result){

    },
    error:function (result)
    {
      handleError();
    }
  });

}


function pad(n, width, z) {
  z = z || '0';
  n = n + '';
  return n.length >= width ? n : new Array(width - n.length + 1).join(z) + n;
}

var statusRefreshInterval;


function activateGisConfig(){
  initProgressBar();
  setTimeout(refreshGisStatus,10);
  statusRefreshInterval = setInterval(refreshGisStatus, 1000);

}

function deactivateGisConfig(){
  clearInterval(statusRefreshInterval);
}

function refreshGisStatus(){
  requestServerForStatus();
  requestCurrentUpdateStatus();
}
var progressbarInitialized = false;

function initProgressBar(){
  if(progressbarInitialized == false){

    gisDataProgressbar = $( "#progressbar" );

    gisDataProgressbar.progressbar({
      value: false,

    });
    var progressbarValue = gisDataProgressbar.find( ".ui-progressbar-value" );
    progressbarValue.css({
      "background": '#3fc117'
    });

    gisDataProgressbar.progressbar( "value", 0 );
    gisDataProgressbar.show();

    progressbarInitialized = true;

    changeUpdatingStatus(true);

  }
}





function setProgressBar(percentage){
  gisDataProgressbar.progressbar( "value", percentage );
}