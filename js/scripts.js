document.addEventListener('DOMContentLoaded', function() {
    var openBtn = document.getElementById('openWarningModal');
    var closeBtn = document.getElementById('closeWarningModal');
    var modal = document.getElementById('warningModal');
    if (openBtn && closeBtn && modal) {
        openBtn.addEventListener('click', function() {
            modal.classList.add('active');
        });
        closeBtn.addEventListener('click', function() {
            modal.classList.remove('active');
        });
        // 바깥 영역 클릭 시 닫기
        modal.addEventListener('click', function(e) {
            if (e.target === modal) {
                modal.classList.remove('active');
            }
        });
    }
});
