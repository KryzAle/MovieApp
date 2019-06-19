<%-- 
    Document   : index
    Created on : 17/06/2019, 13:03:40
    Author     : labctr
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>SOCIO</th>
                    <th>PELICULA</th>
                    <th>FECHA RENTA</th>
                    <th>FECHA HASTA</th>
                    <th>FECHA ENTREGA</th>
                    <th>VALOR</th>
                </tr>
            </thead>
            <tbody>
            <c:forEach var="dato" items="${requestScope.lista}">
               <tr>
                    <td> ${dato.actNombre}</td>

                </tr>
            </c:foreach>
            </tbody>
        </table>
    </body>
</html>
