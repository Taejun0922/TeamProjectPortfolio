document.addEventListener("DOMContentLoaded", function () {
    const openSignupBtns = document.querySelectorAll(".openSignup"); // 회원가입 버튼 여러 개
    const closeSignupBtn = document.getElementById("closeSignup");
    const signupPanel = document.getElementById("signupPanel");
    const overlay = document.getElementById("overlay");
    const loginForm = document.getElementById("loginForm");
    const openLoginBtn = document.getElementById("openLogin");
    const loginToggleBtn = document.querySelector('[data-bs-target="#loginForm"]');

    // 회원가입 버튼 클릭 시 이벤트 추가
    openSignupBtns.forEach(btn => {
        btn.addEventListener("click", function () {
            signupPanel.style.transform = "translateX(0)";
            overlay.classList.remove("d-none"); // 배경 활성화
            if (loginForm) {
                loginForm.classList.remove("show"); // 로그인 폼 닫기
            }
        });
    });

    // 회원가입 패널 닫기
    if (closeSignupBtn) {
        closeSignupBtn.addEventListener("click", function () {
            signupPanel.style.transform = "translateX(100%)";
            overlay.classList.add("d-none"); // 배경 숨김
        });
    }

    // 배경 클릭 시 회원가입 패널 닫기
    if (overlay) {
        overlay.addEventListener("click", function () {
            signupPanel.style.transform = "translateX(100%)";
            overlay.classList.add("d-none");
            if (loginForm) {
                loginForm.classList.remove("show"); // 로그인 폼 닫기
            }
        });
    }

    // 로그인 버튼 클릭 시 회원가입 패널 자동 닫기
    if (loginToggleBtn) {
        loginToggleBtn.addEventListener("click", function () {
            signupPanel.style.transform = "translateX(100%)"; // 회원가입 패널 닫기
            overlay.classList.add("d-none"); // 배경 오버레이 숨기기
        });
    }

    // 로그인 버튼 클릭 시 로그인 폼 보이도록 설정
    if (openLoginBtn) {
        openLoginBtn.addEventListener("click", function () {
            if (loginForm) {
                loginForm.classList.toggle("show");
                loginForm.classList.remove("d-none"); // 로그인 폼 다시 보이기
            }
        });
    }

    // 로그인 폼 검증 함수
    window.validateLoginForm = function () {
        var username = document.querySelector("form[name='loginForm'] input[name='username']").value.trim();
        var password = document.querySelector("form[name='loginForm'] input[name='password']").value.trim();

        if (!username) {
            alert("아이디를 입력해주세요.");
            return false;
        }

        if (!password) {
            alert("비밀번호를 입력해주세요.");
            return false;
        }

        return true;
    };

    // 회원가입 폼 검증 함수
    window.validateSignupForm = function () {
        var memberId = document.querySelector("form[name='registerForm'] input[name='memberId']").value.trim();
        var password = document.querySelector("form[name='registerForm'] input[name='memberPassword']").value.trim();
        var name = document.querySelector("form[name='registerForm'] input[name='memberName']").value.trim();
        var phone = document.querySelector("form[name='registerForm'] input[name='memberPhone']").value.trim();
        var email = document.querySelector("form[name='registerForm'] input[name='memberEmail']").value.trim();

        if (!memberId) {
            alert("아이디를 입력해주세요.");
            return false;
        }
        if (!password) {
            alert("비밀번호를 입력해주세요.");
            return false;
        }
        if (!name) {
            alert("이름을 입력해주세요.");
            return false;
        }
        if (!phone) {
            alert("전화번호를 입력해주세요.");
            return false;
        }
        if (!email) {
            alert("이메일을 입력해주세요.");
            return false;
        }

       return true;
    };

    // 회원 삭제
    window.confirmDelete = function() {
        if (confirm("정말로 회원을 탈퇴하시겠습니까? 이 작업은 되돌릴 수 없습니다.")) {
            document.getElementById("deleteForm").submit();
        }
    }

    document.forms["registerForm"].onsubmit = function() {
        var address1 = document.getElementById("member_address_1").value;
        var address2 = document.getElementById("member_address_2").value;

        // 주소와 상세주소를 합쳐서 memberAddress hidden 필드에 설정
        var fullAddress = address1 + "," + address2;
        document.getElementById("memberAddress").value = fullAddress.trim(); // 공백 제거

        // 폼 제출이 정상적으로 이루어지도록 return true
        return true;
    };

    // 게시판 목록 이동
    const noticeBtn = document.getElementById('notice-btn');
    noticeBtn.addEventListener('click', function() {
        window.location.href = '/CampingMarket/notice/list'; // 이동할 페이지 URL
    });

    // 로그인 실패 메시지 토스트만 자동 실행
    const loginErrorToast = document.getElementById('loginErrorToast');
    if (loginErrorToast) {
        const toastInstance = new bootstrap.Toast(loginErrorToast, {
            delay: 2000
        });
        toastInstance.show();
    }
});
