// 검색 기능 관리
document.addEventListener('DOMContentLoaded', async () => {
    const searchForm = document.getElementById('searchForm');
    const searchInput = document.getElementById('searchInput');
    const searchResults = document.getElementById('searchResults');

    // URL에서 검색어 파라미터 처리
    const urlParams = new URLSearchParams(window.location.search);
    const searchQuery = urlParams.get('q');

    // 검색어가 있으면 검색 수행
    if (searchQuery) {
        searchInput.value = searchQuery;
        try {
            const response = await fetch(`/users?nickname=${encodeURIComponent(searchQuery)}`, {
                credentials: 'include',
                headers: AUTH.getAuthHeader()
            });

            console.log('검색 응답:', response);
            if (!response.ok) {
                const errorText = await response.text();
                console.error('검색 실패 응답:', errorText);
                throw new Error('검색 요청 실패');
            }

            const result = await response.json();
            console.log('검색 결과:', result);

            if (result.status !== 200 || !result.data) {
                throw new Error('검색 결과 없음');
            }

            displaySearchResults(result.data);
        } catch (error) {
            console.error('초기 검색 실패:', error);
            searchResults.innerHTML = '<p class="error-message">검색 중 오류가 발생했습니다.</p>';
        }
    }

    // 검색 폼 제출 처리
    searchForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const searchTerm = searchInput.value.trim();
        if (!searchTerm) return;
        
        try {
            const response = await fetch(`/users?nickname=${encodeURIComponent(searchTerm)}`, {
                credentials: 'include',
                headers: AUTH.getAuthHeader()
            });

            if (!response.ok) {
                const errorText = await response.text();
                console.error('검색 실패 응답:', errorText);
                throw new Error('검색 요청 실패');
            }

            const result = await response.json();
            console.log('검색 결과:', result);

            if (result.status !== 200 || !result.data) {
                throw new Error('검색 결과 없음');
            }

            displaySearchResults(result.data);
        } catch (error) {
            console.error('검색 실패:', error);
            searchResults.innerHTML = '<p class="error-message">검색 중 오류가 발생했습니다.</p>';
        }
    });

    // 검색 결과 표시
    function displaySearchResults(users) {
        if (!Array.isArray(users)) {
            users = [users];
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