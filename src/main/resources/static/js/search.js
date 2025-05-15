// 검색 기능 관리
document.addEventListener('DOMContentLoaded', () => {
    const searchForm = document.getElementById('searchForm');
    const searchInput = document.getElementById('searchInput');
    const searchResults = document.getElementById('searchResults');

    // 검색 폼 제출 처리
    searchForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const searchTerm = searchInput.value.trim();
        
        if (!searchTerm) return;

        try {
            // 토큰 유효성 검사 및 갱신
            await AUTH.ensureValidToken();

            const response = await fetch(`/users?nickname=${encodeURIComponent(searchTerm)}`, {
                headers: {
                    ...AUTH.getAuthHeader()
                }
            });

            if (!response.ok) throw new Error('검색 실패');

            const result = await response.json();
            if (result.status !== 200 || !result.data) {
                throw new Error('검색 실패');
            }

            displaySearchResults(result.data);
        } catch (error) {
            console.error('검색 중 오류 발생:', error);
            searchResults.innerHTML = '<p class="error-message">검색 중 오류가 발생했습니다.</p>';
        }
    });

    // 검색 결과 표시
    function displaySearchResults(users) {
        if (!Array.isArray(users)) {
            users = [users]; // 단일 사용자 결과를 배열로 변환
        }
        
        searchResults.innerHTML = users.map(user => `
            <li class="user-list-item">
                <div class="user-container">
                    <div class="user-info">
                        <img src="${user.profileImage || 'https://via.placeholder.com/40'}" 
                             alt="사용자 프로필" 
                             class="user-profile-image">
                        <span class="user-name">${user.nickname}</span>
                    </div>
                    <button class="follow-button not-following"
                            data-user-id="${user.id}">
                        팔로우
                    </button>
                </div>
            </li>
        `).join('');

        // 팔로우 버튼 이벤트 리스너 추가
        attachFollowButtonListeners();
    }
}); 