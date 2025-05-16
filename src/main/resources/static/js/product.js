document.addEventListener("DOMContentLoaded", () => {
    // 이미지 관련 요소
    let mainProductImg = document.querySelector("#mainProductImg");
    let thumbnailList = document.querySelectorAll("#thumbnailList img");

    // 주문창 관련 요소
    let wishlistIcon = document.querySelector("#wishlistIcon");

    // 상세 정보 보기 관련 요소
    let toggleDetailButton = document.querySelector("#toggleDetail");
    let detailView = document.querySelector("#detailView");
    let detailImage = document.querySelector("#detailImage");
    let isDetailOpen = false;

    // 초기 높이 설정 (원본 크기 유지하면서 일부만 보이게)
    let initialHeight = 500;
    let maxHeight = detailImage.naturalHeight || 1500;
    detailView.style.height = initialHeight + "px";
    detailView.style.overflow = "hidden";

    toggleDetailButton.addEventListener("click", () => {
        if (!isDetailOpen) {
            let expandInterval = setInterval(() => {
                if (detailView.clientHeight < maxHeight) {
                    detailView.style.height = (detailView.clientHeight + 100) + "px";
                } else {
                    clearInterval(expandInterval);
                    toggleDetailButton.textContent = "∧ 상세정보 닫기";
                    isDetailOpen = true;
                }
            }, 10);
        } else {
            let collapseInterval = setInterval(() => {
                if (detailView.clientHeight > initialHeight) {
                    detailView.style.height = (detailView.clientHeight - 100) + "px";
                } else {
                    clearInterval(collapseInterval);
                    toggleDetailButton.textContent = "∨ 상세정보 보기";
                    isDetailOpen = false;
                }
            }, 10);
        }
    });

    // 👉 총 가격 업데이트 함수
    function updateTotalPrice() {
        let quantityInput = document.querySelector("#quantityInput");
        let quantity = parseInt(quantityInput.value);
        let unitPrice = parseInt(document.querySelector("#unitPrice").value);

        // 빈 값 또는 음수, NaN 방지
        if (isNaN(quantity) || quantity < 1) {
            quantity = 0;
        }

        let totalPrice = quantity * unitPrice;

        document.querySelector("#totalPrice").textContent = totalPrice.toLocaleString();
    }

    // 개수 감소 메서드
    function decreaseValue() {
        let input = document.querySelector("#quantityInput");
        let value = parseInt(input.value);
        if (value > 1) {
            input.value = value - 1;
            updateTotalPrice();
        }
    }

    // 개수 증가 메서드
    function increaseValue() {
        let input = document.querySelector("#quantityInput");
        input.value = parseInt(input.value) + 1;
        updateTotalPrice();
    }

    window.decreaseValue = decreaseValue;
    window.increaseValue = increaseValue;

    // 수동으로 수량을 입력했을 때도 가격 업데이트
    document.querySelector("#quantityInput").addEventListener("input", updateTotalPrice);

    document.querySelector("#cartForm").addEventListener("submit", (event) => {
        let quantity = document.querySelector("#quantityInput").value;
        console.log("장바구니 추가, 상품 수량:", quantity);
    });

    // 페이지 로드 시 초기 총 가격 설정
    updateTotalPrice();

    // 👉 직접 주문 버튼 클릭 시 동작
    function submitDirectOrder() {
        const form = document.querySelector("#cartForm");
        const quantityInput = document.querySelector("#quantityInput");
        const productIdInput = form.querySelector("input[name='productId']");
        const quantity = quantityInput?.value;

        if (!quantity || parseInt(quantity) < 1) {
            alert("수량을 1 이상 입력해주세요.");
            return;
        }

        // 폼 action, method 조정
        form.action = "/CampingMarket/order/direct";
        form.method = "get";

        // 숨겨진 필드에 directOrder 표시 (선택사항)
        document.querySelector("#isDirectOrder").value = "true";

        // submit 실행
        form.submit();
    }

    // 전역 함수로 등록 (HTML inline onclick에서 호출 가능하게)
    window.submitDirectOrder = submitDirectOrder;
});
