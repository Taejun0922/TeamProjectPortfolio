// 주소 찾기 버튼 이벤트 - 카카오 라이브러리 활용
// - address(기본 주소), roadAddress(도로명 주소), jibunAddress(지번 주소), zonecode(우편번호)
function findAddress() {
    new daum.Postcode({
        oncomplete: function(data) {
            $("#member_address_1").val(data.address);
            $("#member_address_1_edit").val(data.address);
        }
    }).open();
}

