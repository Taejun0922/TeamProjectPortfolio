// document.addEventListener("DOMContentLoaded", () => {}) 를 사용하지 않아야 함.
// cart 에 도서 추가
function addCart(bookId) {
    if(confirm("장바구니에 도서를 추가하겠습니까?") == true) {
        document.addForm.action = "/BookMarket/cart/book/" + bookId; // 지정 경로로 이동
        document.addForm.submit();
    }
}

// 카트 전체 삭제
function clearCart(cartId) {
   if(confirm("장바구니 전체를 삭제하겠습니까?") == true) {
       document.clearForm.action = "/BookMarket/cart/" + cartId;
       document.clearForm.submit();
       setTimeout('location.reload(true)', 10);
   }
}
// 카트 개별(1건) 삭제
function removeCart(bookId) {
   if(confirm("장바구니 항목을 삭제하겠습니까?") == true) {
       document.removeForm.action = "/BookMarket/cart/book/" + bookId;
       document.removeForm.submit();
       setTimeout('location.reload(true)', 10);
   }
}
document.addEventListener('DOMContentLoaded', function() {
    const decreaseBtn = document.getElementById('decrease-btn');
    const increaseBtn = document.getElementById('increase-btn');
    const quantityInput = document.getElementById('quantity-input');

    // 수량 감소 버튼 클릭 시
//    decreaseBtn.addEventListener('click', function() {
//        let quantity = parseInt(quantityInput.value);
//        if (quantity > 1) {
//            quantityInput.value = quantity - 1;
//        }
//    });

    // 수량 증가 버튼 클릭 시
//    increaseBtn.addEventListener('click', function() {
//        let quantity = parseInt(quantityInput.value);
//        quantityInput.value = quantity + 1;
//    });

    // 수량의 변화와 전체가격 변동하는 코드
    document.querySelectorAll('.increase-btn, .decrease-btn').forEach(btn => {
      btn.addEventListener('click', function () {
        const productId = this.getAttribute('data-product-id');
        const input = document.querySelector(`.quantity-input[data-product-id="${productId}"]`);
        let quantity = parseInt(input.value);

        if (this.classList.contains('increase-btn')) {
          quantity++;
        } else if (this.classList.contains('decrease-btn') && quantity > 1) {
          quantity--;
        }

        input.value = quantity;

        fetch('/cart/update', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify({
            productId: productId,
            quantity: quantity
          })
        })
        .then(res => res.json())
        .then(data => {
          document.getElementById(`price-${productId}`).textContent = data.itemTotalPrice + '원';
          document.getElementById("cart-total-price").textContent = data.cartTotalPrice + '원';
        })
        .catch(err => console.error("AJAX 오류:", err));
      });
    });
});




