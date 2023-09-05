//获取当前浏览器地址中的项目路径
function projectUrl() {

    var curWwwPath = window.document.location.href;
    var pathName = window.document.location.pathname;
    var pos = curWwwPath.indexOf(pathName);
    var localhostPath = curWwwPath.substring(0, pos);

    //输出是 http://localhost:8080
    // console.log(localhostPath);

    //获取带"/"的项目名，如：/shipberOp
    var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
    // console.log(projectName);

    var endpoint = localhostPath+projectName;

    return endpoint;
}


//获取当前浏览器地址中的项目路径(Ws)
function projectWsUrl() {

    if (window.location.protocol == 'https:') {
        var protocol = 'wss://';
    } else {
        var protocol = 'ws://';
    }

    var curWwwPath = window.document.location.href;
    var pathName = window.document.location.pathname;
    var pos = curWwwPath.indexOf(pathName);
    var localhostPath = curWwwPath.substring(0, pos);

    //输出是 http://localhost:8080
    // console.log(localhostPath);

    var ipPort = localhostPath.split("//")[1];
    //输出是 localhost:8080
    // console.log(ipPort);


    //获取带"/"的项目名，如：/shipberOp
    var projectName = pathName.substring(0, pathName.substr(1).indexOf('/') + 1);
    // console.log(projectName);

    var endpoint = protocol+ipPort+projectName;

    return endpoint;
}
