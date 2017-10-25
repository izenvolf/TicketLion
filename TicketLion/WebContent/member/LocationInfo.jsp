<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>Insert title here</title>
<link rel="stylesheet" href="css/location.css">
<link rel="stylesheet" href="css/location2.css">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

</head>
<body>
	<center>
	
	<input type="hidden" value="${bean.l_address }" id="address">
	<input type="hidden" value="${bean.l_name }" id="name">
	<!-- �������� -->
	<div class="boxStyle" margin-bottom="0px;">
		<div class="loctitle" align="left" >${bean.l_name }</div>
			
	<div id="map" style="width:900px;height:350px;margin-bottom:15px;margin-top: 15px;"></div>

<script type="text/javascript" src="//dapi.kakao.com/v2/maps/sdk.js?appkey=1df28531ff08168fada18465099a508e&libraries=services"></script>
<script>

var mapContainer = document.getElementById('map'), // ������ ǥ���� div 
    mapOption = {
        center: new daum.maps.LatLng(33.450701, 126.570667), // ������ �߽���ǥ
        level: 3 // ������ Ȯ�� ����
    };  

// ������ �����մϴ�    
var map = new daum.maps.Map(mapContainer, mapOption); 

// �ּ�-��ǥ ��ȯ ��ü�� �����մϴ�
var geocoder = new daum.maps.services.Geocoder();
// �ּҷ� ��ǥ�� �˻��մϴ�
geocoder.addressSearch(document.getElementById('address').value, function(result, status) {

    // ���������� �˻��� �Ϸ������ 
     if (status === daum.maps.services.Status.OK) {

        var coords = new daum.maps.LatLng(result[0].y, result[0].x);

        // ��������� ���� ��ġ�� ��Ŀ�� ǥ���մϴ�
        var marker = new daum.maps.Marker({
            map: map,
            position: coords
        });

        // ����������� ��ҿ� ���� ������ ǥ���մϴ�
        var infowindow = new daum.maps.InfoWindow({
            content: '<div style="width:150px;text-align:center;padding:6px 0;">'+document.getElementById('name').value+'</div>'
        });
        infowindow.open(map, marker);

        // ������ �߽��� ��������� ���� ��ġ�� �̵���ŵ�ϴ�
        map.setCenter(coords);
    } 
});    
</script>
	<!-- ������ -->
	
<!-- 	<form action="LocationMain.jsp?center=LocationMap.jsp" method="post"> -->
<!-- 		<table align="center" width="600px" border="0" style="margin-bottom:100px"> -->
<!-- 			<tr align="center"> -->

			<div class="detail" align="left" >���ּ�</div>
			<div class="add" align="left">${bean.l_address }</div>
			
			<input style="font-family: 'Jeju Gothic', sans-serif" class="button" type="button" value="���ư���" onclick="history.back(-1)">
			
			</div></center>
			
<!-- 				<th>�ּ�</th> -->
<!-- 			</tr> -->
<!-- 			<tr> -->
<!-- 				<td align="center" width="200"> -->
<%-- 					${bean.l_name} --%>
<!-- 				</td>  -->
<!-- 				<td align="center" width="200"> -->
<%-- 					${bean.l_address } --%>
<!-- 				</td> -->
<!-- 				</tr> -->
			
			
<!-- 			<tr> -->
<!-- 				<td align="center" width="200">�뿩�ݾ�</td> -->
<!-- 				<td align="center" width="200"> -->
<%-- 					${bean.carprice } --%>
<!-- 				</td> -->
<!-- 			</tr>			 -->
<!-- 			<tr> -->
<!-- 				<td align="center" width="200">����ȸ��</td> -->
<!-- 				<td align="center" width="200"> -->
<%-- 					${bean.carcompany } --%>
<!-- 				</td> -->
<!-- 			</tr>			 -->




<!-- 			<tr> -->
<!-- 				<td align="center" width="200"> -->
<%-- 					<input type="hidden" name="l_num" value="${bean.l_num}"> --%>
<%-- 					<input type="hidden" name="l_img" value="${bean.l_img }"> --%>
<%-- 					<input type="hidden" name="l_name" value="${bean.l_name }"> --%>
<!-- 				</td> -->
				
				
<!-- 			</tr>			 -->
<!-- 		</table> -->
<!-- 	</form>	 -->
	<p>


 
</body>
</html>



