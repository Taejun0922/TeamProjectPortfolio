// 수정 폼 표시 토글
function showEditForm() {
  const editForm = document.getElementById("editForm");
  editForm.style.display = (editForm.style.display === "none" || editForm.style.display === "") ? "block" : "none";
}

// 결제하기 버튼 이벤트
function showSuccessAlert() {
  alert('주문이 완료되었습니다.');
  return true; // 폼 제출 허용
}


//fetch('/order', {
//  method: 'POST',
//  body: new FormData(event.target)
//})
//.then(response => {
//  if (response.ok) {
//    alert('주문이 완료되었습니다.');
//    window.location.href = '/CampingMarket/main'; // 직접 리다이렉트
//  } else {
//    alert('주문 처리 중 오류가 발생했습니다.');
//  }
//})
//.catch(err => {
//  console.error(err);
//  alert('서버 오류가 발생했습니다.');
//});
