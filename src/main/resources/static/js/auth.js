// 토큰 관리
const AUTH = {
    // 현재 사용자 ID 가져오기
    getCurrentUserId: async () => {
        try {
            const response = await fetch('/users/me', {
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            
            if (!response.ok) {
                throw new Error('Failed to get current user');
            }
            
            const data = await response.json();
            return data.data.id;
        } catch (error) {
            console.error('Failed to get user ID:', error);
            return null;
        }
    },

    // 로그아웃
    logout: () => {
        fetch("/users/logout", {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/json"
            }
        }).finally(() => {
            alert("성공적으로 로그아웃 되었습니다.");
            window.location.replace("/login");
        });
    }
}; 

// 페이지 로드시 기본 설정
document.addEventListener('DOMContentLoaded', () => {
    // 로그아웃 버튼 이벤트 리스너
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', AUTH.logout);
    }
}); 