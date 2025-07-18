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
    if (detailView && detailImage && toggleDetailButton) {
        // 초기 높이 설정
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
    }

    // 👉 총 가격 업데이트 함수
    function updateTotalPrice() {
        const quantityInput = document.querySelector("#quantityInput");
        const unitPriceInput = document.querySelector("#unitPrice");

        if (!quantityInput || !unitPriceInput) {
            console.warn("필요한 요소가 없습니다: #quantityInput 또는 #unitPrice");
            return;
        }

        let quantity = parseInt(quantityInput.value);
        let unitPrice = parseInt(unitPriceInput.value);

        if (isNaN(quantity) || quantity < 1) {
            quantity = 0;
        }

        let totalPrice = quantity * unitPrice;

        const totalPriceElem = document.querySelector("#totalPrice");
        if (totalPriceElem) {
            totalPriceElem.textContent = totalPrice.toLocaleString();
        }
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
    const quantityInput = document.querySelector("#quantityInput");
    if (quantityInput) {
      quantityInput.addEventListener("input", updateTotalPrice);
    }

    // 장바구니 토스트를 수동으로 제어할 수 있게 함수 등록
    window.showCartToast = function () {
        const toastEl = document.getElementById('cartToast');
        if (toastEl) {
            const toast = new bootstrap.Toast(toastEl, {
                delay: 2000
            });
            toast.show();
        }
    };

    // 카트로 데이터 보내기
    const cartForm = document.querySelector("#cartForm");
    if (cartForm) {
        cartForm.addEventListener("submit", async (event) => {
            event.preventDefault(); // 페이지 이동 막기

            const productId = cartForm.querySelector("input[name='productId']").value;
            const quantity = cartForm.querySelector("#quantityInput").value;

            if (!quantity || parseInt(quantity) < 1) {
                alert("수량을 1 이상 입력해주세요.");
                return;
            }

            try {
                const response = await fetch("/CampingMarket/cart", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
                    },
                    body: new URLSearchParams({
                        productId: productId,
                        quantity: quantity
                    })
                });

                const responseText = await response.text();

                if (response.ok) {
                    console.log("서버 응답:", responseText);

                    if (typeof showCartToast === "function") {
                        showCartToast();
                    }
                } else {
                    alert("장바구니 추가 실패: " + responseText);
                }
            } catch (error) {
                console.error("AJAX 요청 중 오류 발생:", error);
                alert("장바구니 추가 중 오류가 발생했습니다.");
            }
        });
    }

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

    // 🧩 카테고리별 가장 큰 번호 조회 + ID 생성
    async function generateProductId() {
        console.log("[DEBUG] generateProductId() 호출됨");

        const categoryInput = document.querySelector("input[name='productCategory']:checked");
        if (!categoryInput) {
            console.warn("[DEBUG] 선택된 카테고리 없음. 함수 종료.");
            return;
        }

        const category = categoryInput.value;
        console.log("[DEBUG] 선택된 카테고리:", category);

        const prefix = category.replace(/\s+/g, "_");
        console.log("[DEBUG] prefix:", prefix);

        try {
            const url = `/CampingMarket/admin/products/max-id?category=${encodeURIComponent(category)}`;
            console.log("[DEBUG] fetch URL:", url);

            const resp = await fetch(url);
            console.log("[DEBUG] fetch 응답 상태:", resp.status);

            if (!resp.ok) throw new Error("Max ID 조회 실패");

            const json = await resp.json();
            console.log("[DEBUG] 서버 응답 JSON:", json);

            const nextNumber = json.nextNumber;
            if (typeof nextNumber === 'undefined') {
                console.warn("[DEBUG] nextNumber가 undefined입니다. 서버 응답 확인 필요.");
                return;
            }

            const generatedId = `${prefix}_${nextNumber}`;
            const productIdField = document.querySelector("#productId");

            if (productIdField) {
                productIdField.value = generatedId;
                console.log("[DEBUG] 설정된 productId:", generatedId);
            } else {
                console.warn("[DEBUG] #productId 요소를 찾을 수 없습니다.");
            }
        } catch (err) {
            console.error("[ERROR] generateProductId() 실패:", err);
            alert("상품 ID 자동 생성에 실패했습니다.");
        }
    }


    // 카테고리 선택 시 자동 재생성
    document.querySelectorAll("input[name='productCategory']").forEach(radio => {
        radio.addEventListener("change", generateProductId);
    });

    // 페이지 로드 시 첫 선택 또는 기본값 있을 경우 자동 생성
    generateProductId();

    // 상품추가 토스트 메시지 코드
    function showRegisterSuccessToastIfNeeded() {
        const urlParams = new URLSearchParams(window.location.search);
        if (urlParams.get("registerSuccess") === "true") {
            const toastEl = document.getElementById("registerToast");
            if (toastEl) {
                const toast = new bootstrap.Toast(toastEl, { delay: 3000 });
                toast.show();
            }
        }
    }

    showRegisterSuccessToastIfNeeded();

    // 삭제 버튼 클릭 시 confirm 후 삭제 폼 제출 함수
        function confirmDelete() {
            if (confirm('정말 삭제하시겠습니까?')) {
                const deleteForm = document.getElementById('productDeleteForm');
                if (deleteForm) {
                    deleteForm.submit();
                } else {
                    console.warn("삭제 폼이 존재하지 않습니다.");
                }
            }
        }

        // 삭제 버튼에 이벤트 연결
        const deleteBtn = document.getElementById("deleteBtn");
        if (deleteBtn) {
            deleteBtn.addEventListener("click", (e) => {
                e.preventDefault();
                confirmDelete();
            });
        }
});
