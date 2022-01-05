<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="excelbb.controller.HomeController" %>
<html language="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>

<script>
function sendJSON(url, content){
            let xhr = new XMLHttpRequest();
            xhr.open("POST", url, true);
            xhr.setRequestHeader("Content-Type", "application/json");
            xhr.onreadystatechange = function () {
                if (xhr.readyState === 4 && xhr.status === 200) {
                    if(url ==='query'){
                        //alert('received'+this.responseText);
                        document.querySelector('#content').value =  this.responseText;
                    }
                }
            };
            var data = JSON.stringify({ "content": content});
            xhr.send(data);
        }

        function dump() {
            let content = document.querySelector('#content').value;
            sendJSON("dump", content);
        }

        function query() {
            let content = document.querySelector('#keywords').value;
            if(content!=''){
                sendJSON("query", content);
            }
        }
</script>
<body>
    <div>
        <table>
            <tr>
                <td>loaded excel </td>
                <td><%=HomeController.excelPath%></td>
            </tr>
        </table>

        <table>

        <tr>
        <td>command</td>
        <td>

        <input type="text" name="keywords" id="keywords" list="keywordOptions"  onchange="query()">
        <datalist id="keywordOptions">
          <option value="Boston">
          <option value="Cambridge">
        </datalist>

        </td>
        </tr>
        <tr>
        <td>content</td>

        <td><textarea cols="150" rows="20" name="content" id="content"></textarea> </td>
        </tr>
        <tr>
        <td></td><td><input type="button" name="submit"  value="dump"  onclick="dump()"></input></td>
        </tr>
        </table>


    </div>

</body>
</html>
