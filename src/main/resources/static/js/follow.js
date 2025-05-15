// 팔로우 기능 관리
function attachFollowButtonListeners() {
    document.querySelectorAll('.follow-button').forEach(button => {
        button.addEventListener('click', async () => {
            const userId = button.dataset.userId;
            const isFollowing = button.classList.contains('following');

            try {
                // 토큰 유효성 검사 및 갱신
                await AUTH.ensureValidToken();

                const response = await fetch(`/follow${isFollowing ? `/${userId}` : ''}`, {
                    method: isFollowing ? 'DELETE' : 'POST',
                    headers: {
                        ...AUTH.getAuthHeader(),
                        'Content-Type': 'application/json'
                    },
                    body: !isFollowing ? JSON.stringify({
                        followingId: userId
                    }) : null
                });

                if (!response.ok) throw new Error('팔로우 상태 변경 실패');

                // 버튼 상태 토글
                button.classList.toggle('following');
                button.classList.toggle('not-following');
                button.textContent = isFollowing ? '팔로우' : '팔로잉';
            } catch (error) {
                console.error('팔로우 처리 중 오류 발생:', error);
            }
        });
    });
}

// 팔로우 목록 관리
document.addEventListener('DOMContentLoaded', async () => {
    const followersListElem = document.getElementById('followers-list');
    const followingListElem = document.getElementById('following-list');
    const emptyStateElem = document.querySelector('.empty-state');
    
    // 탭 전환 기능
    document.querySelectorAll('.follow-nav-button').forEach(button => {
        button.addEventListener('click', () => {
            // 활성 탭 스타일 변경
            document.querySelectorAll('.follow-nav-button').forEach(btn => {
                btn.classList.remove('active');
            });
            button.classList.add('active');

            // 목록 표시 전환
            const targetTab = button.dataset.tab;
            followersListElem.style.display = targetTab === 'followers' ? 'block' : 'none';
            followingListElem.style.display = targetTab === 'following' ? 'block' : 'none';
        });
    });

    // 현재 사용자 ID 가져오기
    const currentUserId = AUTH.getCurrentUserId();

    // 팔로워/팔로잉 목록 로드
    async function loadFollowList(type) {
        try {
            await AUTH.ensureValidToken();

            const response = await fetch(`/follow/${currentUserId}/${type}`, {
                headers: {
                    ...AUTH.getAuthHeader()
                }
            });

            if (!response.ok) throw new Error(`${type} 목록 로드 실패`);

            const result = await response.json();
            if (result.status !== 200 || !result.data) {
                throw new Error(`${type} 목록 로드 실패`);
            }

            const users = result.data;
            const targetElement = type === 'followers' ? followersListElem : followingListElem;

            if (users.length === 0) {
                targetElement.innerHTML = '<p class="empty-message">목록이 비어있습니다.</p>';
                return;
            }

            targetElement.innerHTML = users.map(user => `
                <li class="user-list-item">
                    <div class="user-container">
                        <div class="user-info">
                            <img src="${user.profileImage || 'https://via.placeholder.com/40'}" 
                                 alt="사용자 프로필" 
                                 class="user-profile-image">
                            <span class="user-name">${user.nickname}</span>
                        </div>
                        <button class="follow-button ${user.isFollowing ? 'following' : 'not-following'}"
                                data-user-id="${user.id}">
                            ${user.isFollowing ? '팔로잉' : '팔로우'}
                        </button>
                    </div>
                </li>
            `).join('');

            attachFollowButtonListeners();
        } catch (error) {
            console.error(`${type} 목록 로드 중 오류 발생:`, error);
        }
    }

    // 초기 데이터 로드
    await loadFollowList('followers');
    await loadFollowList('following');
}); 