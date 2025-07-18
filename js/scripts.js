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

// 다운로드 후 자동 설치 화면 열기 (안드로이드)
async function downloadAndAutoInstall(fileUrl, fileName) {
    try {
        // 파일 다운로드
        const response = await fetch(fileUrl);
        const blob = await response.blob();
        
        // 파일 다운로드 실행
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = fileName;
        link.click();
        
        // 안드로이드에서 다운로드 완료 후 설치 화면 열기
        if (/Android/i.test(navigator.userAgent)) {
            setTimeout(() => {
                // 다운로드 폴더 열기 (설치 화면으로 이동)
                try {
                    window.open('content://com.android.externalstorage.documents/document/primary:Download');
                } catch (e) {
                    // 대체 방법: 파일 매니저 열기
                    window.open('file:///storage/emulated/0/Download/');
                }
            }, 3000); // 3초 후 실행
        }
        
        // 메모리 정리
        window.URL.revokeObjectURL(url);
    } catch (error) {
        console.error('다운로드 실패:', error);
        // 실패 시 기본 다운로드 방식으로 fallback
        const link = document.createElement('a');
        link.href = fileUrl;
        link.download = fileName;
        link.click();
    }
}
