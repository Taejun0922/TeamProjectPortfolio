document.addEventListener("DOMContentLoaded", () => {
    const container = document.getElementById("topCarousel");
    const prevBtn = document.getElementById("prevBtn");
    const nextBtn = document.getElementById("nextBtn");

    let scrollAmount = 0;
    const itemWidth = document.querySelector(".col-2").offsetWidth; // 한 아이템의 너비

    nextBtn.addEventListener("click", function() {
        if (scrollAmount < container.scrollWidth - container.clientWidth) {
            scrollAmount += itemWidth;
            container.scrollLeft = scrollAmount;
        }
    });

    prevBtn.addEventListener("click", function() {
        if (scrollAmount > 0) {
            scrollAmount -= itemWidth;
            container.scrollLeft = scrollAmount;
        }
    });


/* 본문 슬라이더 */

    let carousel = document.querySelector("#middleCarousel");
    let carouselInstance = new bootstrap.Carousel(carousel, {
        wrap: true // 끝에서 다시 처음으로 돌아가기
    });

    let nextButton = document.querySelector(".carousel-control-next");
    let prevButton = document.querySelector(".carousel-control-prev");

    let moveCount = 1; // 한 번에 이동할 개수 (여기서 숫자를 바꾸면 이동 개수 조절 가능!)

    nextButton.addEventListener("click", function () {
        for (let i = 0; i < moveCount; i++) {
            carouselInstance.next(); // moveCount 만큼 반복해서 다음으로 이동
        }
    });

    prevButton.addEventListener("click", function () {
        for (let i = 0; i < moveCount; i++) {
            carouselInstance.prev(); // moveCount 만큼 반복해서 이전으로 이동
        }
    });
});
