// 토큰 관리
const AUTH = {
    // Access Token 관리
    getAccessToken: () => {
        return localStorage.getItem('accessToken');
    },

    setAccessToken: (token) => {
        localStorage.setItem('accessToken', token);
    },

    removeAccessToken: () => {
        localStorage.removeItem('accessToken');
    },

    // Refresh Token 관리
    getRefreshToken: () => {
        return localStorage.getItem('refreshToken');
    },

    setRefreshToken: (token) => {
        localStorage.setItem('refreshToken', token);
    },

    removeRefreshToken: () => {
        localStorage.removeItem('refreshToken');
    },

    // 모든 토큰 설정
    setTokens: (accessToken, refreshToken) => {
        AUTH.setAccessToken(accessToken);
        AUTH.setRefreshToken(refreshToken);
    },

    // 모든 토큰 제거
    clearTokens: () => {
        AUTH.removeAccessToken();
        AUTH.removeRefreshToken();
    },

    // 현재 사용자 ID 가져오기
    getCurrentUserId: () => {
        const accessToken = AUTH.getAccessToken();
        if (!accessToken) return null;
        
        try {
            const payload = JSON.parse(atob(accessToken.split('.')[1]));
            return payload.userId; // JWT 토큰에서 userId 추출
        } catch (error) {
            console.error('Failed to get user ID from token:', error);
            return null;
        }
    },

    // API 요청시 사용할 헤더
    getAuthHeader: () => {
        const accessToken = AUTH.getAccessToken();
        return accessToken ? { 'Authorization': `Bearer ${accessToken}` } : {};
    },

    // URL에서 토큰 파라미터 확인 및 저장
    checkAndStoreTokensFromUrl: () => {
        const urlParams = new URLSearchParams(window.location.search);
        const accessToken = urlParams.get('accessToken');
        const refreshToken = urlParams.get('refreshToken');

        if (accessToken) {
            AUTH.setAccessToken(accessToken);
        }
        if (refreshToken) {
            AUTH.setRefreshToken(refreshToken);
        }

        return !!accessToken;
    },

    // 토큰 리프레시
    refreshAccessToken: async () => {
        try {
            const refreshToken = AUTH.getRefreshToken();
            if (!refreshToken) {
                throw new Error('Refresh token not found');
            }

            const response = await fetch('/api/auth/refresh', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${refreshToken}`
                }
            });

            if (!response.ok) {
                throw new Error('Token refresh failed');
            }

            const data = await response.json();
            AUTH.setAccessToken(data.accessToken);
            if (data.refreshToken) {
                AUTH.setRefreshToken(data.refreshToken);
            }

            return data.accessToken;
        } catch (error) {
            console.error('Token refresh failed:', error);
            AUTH.clearTokens();
            throw error;
        }
    },

    // 토큰 유효성 확인 및 필요시 리프레시
    ensureValidToken: async () => {
        // URL에서 토큰 체크
        AUTH.checkAndStoreTokensFromUrl();
        
        const accessToken = AUTH.getAccessToken();
        if (!accessToken) {
            throw new Error('No access token');
        }

        try {
            const payload = JSON.parse(atob(accessToken.split('.')[1]));
            const expirationTime = payload.exp * 1000;
            
            if (Date.now() >= expirationTime - (10 * 60 * 1000)) {
                return await AUTH.refreshAccessToken();
            }
            
            return accessToken;
        } catch (error) {
            console.error('Token validation failed:', error);
            return await AUTH.refreshAccessToken();
        }
    },

    initTokenFromUrl: function () {
        const urlParams = new URLSearchParams(window.location.search);
        const accessToken = urlParams.get("accessToken");
        const refreshToken = urlParams.get("refreshToken");

        if (accessToken && refreshToken) {
            localStorage.setItem("accessToken", accessToken);
            localStorage.setItem("refreshToken", refreshToken);

            // URL에서 토큰 제거 (보안 및 깔끔한 주소)
            const url = new URL(window.location);
            url.searchParams.delete("accessToken");
            url.searchParams.delete("refreshToken");
            window.history.replaceState({}, document.title, url.pathname);
        }
    },
}; 

// 페이지 로드시 인증 초기화
document.addEventListener('DOMContentLoaded', AUTH.initAuth); 