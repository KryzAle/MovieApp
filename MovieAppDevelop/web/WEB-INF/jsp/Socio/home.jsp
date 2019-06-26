<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8" />
        <title>Socios</title>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" />
    </head>
    <body>
        <div class="container">
            <div class="row">
                <h1>Socios</h1>
                <p>
                    <a href="<c:url value="add.htm" />" class="btn btn-success"><span class="glyphicon glyphicon-plus" aria-hidden="true"></span> Agregar</a>
                </p>
                <table class="table table-bordered table-striped table-hover">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Cedula</th>
                            <th>Nombre</th>
                            <th>Direccion</th>
                            <th>Telefono</th>
                            <th>Correo</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${datos}" var="datos">
                            <tr>
                                <td><c:out value="${datos.soc_id}" /></td>
                                <td><c:out value="${datos.soc_cedula}" /></td>
                                <td><c:out value="${datos.soc_nombre}" /></td>
                                <td><c:out value="${datos.soc_direccion}" /></td>
                                <td><c:out value="${datos.soc_telefono}" /></td>
                                <td><c:out value="${datos.soc_correo}" /></td>
                                <td>
                                    <a href="<c:url value="edit.htm?id=${datos.soc_id}" />"><span class="glyphicon glyphicon-pencil" aria-hidden="true"></span></a>
                                    <a href="<c:url value="delete.htm?id=${datos.soc_id}" />"><span class="glyphicon glyphicon-trash" aria-hidden="true"></span></a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
            
        </div>
    </body>
</html>
