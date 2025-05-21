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

// 총 결제 금액 계산 및 표시
function calculateTotalPrice() {
  const items = document.querySelectorAll("#orderItems .list-group-item");
  let total = 0;

  items.forEach(item => {
    const price = parseInt(item.dataset.price, 10);
    if (!isNaN(price)) {
      total += price;
    }
  });

  const totalPriceText = document.getElementById("totalPriceText");
  if (totalPriceText) {
    totalPriceText.textContent = `${total.toLocaleString('ko-KR')}원`;
  }
}

// 페이지 로드 시 계산 실행
document.addEventListener("DOMContentLoaded", function () {
  calculateTotalPrice();
});
