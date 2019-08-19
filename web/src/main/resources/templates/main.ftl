<!doctype html>
<html lang="en">
<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet"
          href="https://stackpath.bootstrapcdn.com/bootstrap/4.2.1/css/bootstrap.min.css"
          integrity="sha384-GJzZqFGwb1QTTN6wy59ffF1BuGJpLSa9DkKMp0DgiMDm4iYMj70gZWKYbI706tWS"
          crossorigin="anonymous">

    <link rel="stylesheet" href="./css/bootstrap.min.css">
    <link rel="stylesheet" href="./css/loading.css">

    <!-- Latest compiled and minified CSS -->
    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.13.2/css/bootstrap-select.min.css">

    <style>
        #container {
            margin-top: 10px;
        }
        .content {
            margin-top: 1em;
            padding-top: 1em;
            border-top: 1px dashed #aaa;
            border-bottom: 1px dashed #aaa;
        }
        .reload {
            width: 100%;
            margin-top: 1em;
            text-align: center;
        }
        .load-wrapper {
            margin-top: 1em;
            width: 100%;
            text-align: center;
            line-height: 2.5em;
        }
        .btn-reload {
            float: left;
        }
        #reloadProcess {
            float: right;
        }
        .bootstrap-select .dropdown-menu.inner {
            width: 100px;
            padding-bottom: 1em;
        }
        .dropdown-menu.show {
            display: inline-grid;
        }
    </style>

    <title>Lets hotfix</title>
</head>
<body>

<div class="container" id="container">
    <div class="dropdown">
        <select id="hostNameSelector" data-style="btn-secondary" class="selectpicker" data-live-search="true">
            <#list instances as instance>
                <option value="${instance.homePageUrl}"
                        <#if instance.hostName == hostname>selected</#if>>${instance.hostName}</option>
            </#list>
        </select>

        <a href="https://github.com/liuzhengyang/lets-hotfix" class="github-corner"
           aria-label="View source on GitHub">
            <svg
                    width="80" height="80" viewBox="0 0 250 250"
                    style="fill:#151513; color:#fff; position: absolute; top: 0; border: 0; right: 0;"
                    aria-hidden="true">
                <path d="M0,0 L115,115 L130,115 L142,142 L250,250 L250,0 Z"></path>
                <path d="M128.3,109.0 C113.8,99.7 119.0,89.6 119.0,89.6 C122.0,82.7 120.5,78.6 120.5,78.6 C119.2,72.0 123.4,76.3 123.4,76.3 C127.3,80.9 125.5,87.3 125.5,87.3 C122.9,97.6 130.6,101.9 134.4,103.2"
                      fill="currentColor" style="transform-origin: 130px 106px;"
                      class="octo-arm"></path>
                <path d="M115.0,115.0 C114.9,115.1 118.7,116.5 119.8,115.4 L133.7,101.6 C136.9,99.2 139.9,98.4 142.2,98.6 C133.8,88.0 127.5,74.4 143.8,58.0 C148.5,53.4 154.0,51.2 159.7,51.0 C160.3,49.4 163.2,43.6 171.4,40.1 C171.4,40.1 176.1,42.5 178.8,56.2 C183.1,58.6 187.2,61.8 190.9,65.4 C194.5,69.0 197.7,73.2 200.1,77.6 C213.8,80.2 216.3,84.9 216.3,84.9 C212.7,93.1 206.9,96.0 205.4,96.6 C205.1,102.4 203.0,107.8 198.3,112.5 C181.9,128.9 168.3,122.5 157.7,114.1 C157.9,116.9 156.7,120.9 152.7,124.9 L141.0,136.5 C139.8,137.7 141.6,141.9 141.8,141.8 Z"
                      fill="currentColor" class="octo-body"></path>
            </svg>
        </a>
        <style>.github-corner:hover .octo-arm {
                animation: octocat-wave 560ms ease-in-out
            }
            @keyframes octocat-wave {
                0%, 100% {
                    transform: rotate(0)
                }
                20%, 60% {
                    transform: rotate(-25deg)
                }
                40%, 80% {
                    transform: rotate(10deg)
                }
            }
            @media (max-width: 500px) {
                .github-corner:hover .octo-arm {
                    animation: none
                }
                .github-corner .octo-arm {
                    animation: octocat-wave 560ms ease-in-out
                }
            }</style>
    </div>
    <form method="post" enctype="multipart/form-data" id="reloadForm">
        <div class="content">
            <div class="form-group">
                <label for="classFile" class="col-sm-2 col-form-label">Target Class File</label>
                <input type="file" id="file" class="col-sm-9" name="file"/>
            </div>
            <div class="form-group">
                <label for="targetPid" class="col-sm-2 col-form-label">Target Process</label>
                <select id="targetPid" class="selectpicker col-sm-8" name="targetPid"
                        data-live-search="true"
                        form="reloadForm">
                    <option>Choose...</option>
                </select>
                <button id="reloadProcess" type="button" class="btn
                btn-secondary">RefreshProcess</button>
            </div>
        </div>
        <div class="reload">
            <button type="submit" class="btn btn-primary btn-reload" id="reloadButton">ReloadClass
            </button>
            <div class="load-wrapper">
                <div id="loading-status" class="ld ld-ring ld-cycle"
                     style="visibility:hidden;"></div>
                <span id="result"></span>
            </div>

        </div>

    </form>

</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.3.1/jquery.min.js"
        integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8="
        crossorigin="anonymous"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"
        integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49"
        crossorigin="anonymous"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js"
        integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy"
        crossorigin="anonymous"></script>
<!-- Latest compiled and minified JavaScript -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.13.2/js/bootstrap-select.min.js"></script>
<script>
    $(function () {
        $('.selectpicker').selectpicker();
        $("#hostNameSelector").on("loaded.bs.select", function () {
            console.log("loaded ");
            reloadProcessList();
        });
        $("#hostNameSelector").on("changed.bs.select", function (e, clickedIndex, isSelected, previousValue) {
            console.log("loaded ");
            reloadProcessList();
        });
        $('#reloadForm').on('submit', function (e) {
            e.preventDefault();
            var form = $("#reloadForm")[0];
            var data = new FormData(form);
            data.append('file', $("#file")[0].files[0])
            // if the validator does not prevent form submit
            var url = "/hotfix";
            var hostname = "${hostname}";
            console.log("Selected " + $("#hostNameSelector").val());
            if (!$("#hostNameSelector").val().toLowerCase().includes(hostname.toLowerCase())) {
                console.log("Not Equals " + hostname + " " + $("#hostNameSelector").val())
                data.append("proxyServer", $("#hostNameSelector").val())
            }
            $("#result").html("")
            $("#loading-status").attr("style", "visibility:visible");
            $("#reloadButton").attr("disabled", "true");
            // POST values in the background the the script URL
            $.ajax({
                type: "POST",
                method: "POST",
                url: url,
                data: data,
                contentType: false,
                processData: false,
                cache: false,
                success: function (data) {
                    console.log('result ' + data)
                    var code = data.code;
                    if (code !== 0) {
                        console.log('error ' + e)
                        resetReloadStatus();
                        $("#result").html("Oops, 出错了，请查看具体错误~" + data.msg)
                    } else {
                        resetReloadStatus();
                        $("#result").html("恭喜！reload " + data.data.targetClass + "成功!")
                    }
                },
                error: function (e) {
                    console.log('error ' + e)
                    resetReloadStatus();
                    $("#result").html("Oops, 出错了，请查看具体错误~" + e)
                }
            });
            return false;
        });
        function resetReloadStatus() {
            $("#loading-status").attr("style", "visibility:hidden");
            $("#reloadButton").removeAttr("disabled");
        }
        $("#reloadProcess").click(function () {
            reloadProcessList();
        });
        function reloadProcessList() {
            var data = {};
            var hostname = "${hostname}";
            if (!$("#hostNameSelector").val().toLowerCase().includes(hostname.toLowerCase())) {
                console.log("Not Equals " + hostname + " " + $("#hostNameSelector").val())
                data.proxyServer=$("#hostNameSelector").val();
            };
            $.get('/processList', data, function (data) {
                console.log("process list " + data);
                $("#targetPid").children().remove();
                data['data'].forEach(function (process) {
                    console.log("process append " + process);
                    $("#targetPid").append("<option value=" + process.pid + ">" + process
                            .pid + ' ' + process.displayName + ' ' + process.detailVmArgs +
                        "</option>");
                });
                $('.selectpicker').selectpicker('refresh');
            })
        }
    });
</script>

</body>
</html>