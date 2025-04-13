document.addEventListener("DOMContentLoaded", () => {
    const productContainer = document.getElementById("productContainer");
    const categoryButtons = document.querySelectorAll(".category-btn");

    //console.log("productData:", productData);

    // 기본 카테고리 productCategory_1
    updateProducts("productCategory_1");

    categoryButtons.forEach(button => {
        button.addEventListener("click", function () {
            window.productCategory = button.dataset.productCategory;
            updateProducts(productCategory);
        });
    });

    function updateProducts(productCategory) {
        const products = productData[productCategory] || [];
        productContainer.innerHTML = ""; // 기존 상품 초기화

        products.forEach(product => {
            const productHTML = `
                <div class="col">
                    <a href="/CampingMarket/product/${product.productId}" class="text-decoration-none text-dark">
                        <div class="card shadow-sm">
                            <img src="/CampingMarket/productImages/IMG_${product.productId}.jpg" class="card-img-top">
                            <div class="card-body">
                                <h6 class="card-title">${product.productName}</h6>
                                <p class="text-muted small mb-0">${product.productDescription}</p>
                                <p class="text-muted mb-0">${product.productPrice}원</p>
                            </div>
                        </div>
                    </a>
                </div>
            `;
            productContainer.innerHTML += productHTML;
        });
    }
});
