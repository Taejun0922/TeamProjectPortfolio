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
    let initialHeight = 500;  // 처음 보이는 높이
    let maxHeight = detailImage.naturalHeight || 1500;  // 실제 원본 이미지 크기
    detailView.style.height = initialHeight + "px";
    detailView.style.overflow = "hidden";


    // 상세정보 보기/닫기 버튼 클릭 이벤트 (부드럽게 아래로 확장)
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

    function decreaseValue() {
        let input = document.querySelector("#quantityInput");
        let value = parseInt(input.value);
        if (value > 1) input.value = value - 1;
    }

    function increaseValue() {
        let input = document.querySelector("#quantityInput");
        input.value = parseInt(input.value) + 1;
    }

    window.decreaseValue = decreaseValue;
    window.increaseValue = increaseValue;

    /*document.querySelector(".btn-outline-secondary:first-of-type").addEventListener("click", decreaseValue);
    document.querySelector(".btn-outline-secondary:last-of-type").addEventListener("click", increaseValue);*/

    document.querySelector("#cartForm").addEventListener("submit", (event) => {
        let quantity = document.querySelector("#quantityInput").value;
        console.log("장바구니 추가, 상품 수량:", quantity);
    });
});
