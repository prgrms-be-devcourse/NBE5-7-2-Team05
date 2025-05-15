// 전역 변수 선언
let followersListElem;
let followingListElem;

// 팔로워/팔로잉 목록 로드 함수
async function loadFollowList(type) {
    // URL에서 사용자 ID 가져오기
    const urlParams = new URLSearchParams(window.location.search);
    const userId = urlParams.get('userId');
    
    if (!userId) {
        console.error('사용자 ID가 없습니다.');
        return;
    }

    console.log(`${type} 목록 로드 시작:`, userId);
    
    try {
        const response = await fetch(`/follow/${userId}/${type === 'following' ? 'followings' : 'followers'}`, {
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) {
            const errorText = await response.text();
            console.error(`${type} 응답:`, response.status, errorText);
            throw new Error(`${type} 목록 로드 실패`);
        }

        const result = await response.json();
        console.log(`${type} 응답 데이터:`, result);
        
        if (!result.data) {
            throw new Error(`${type} 목록 데이터 없음`);
        }

        const users = result.data;
        const targetElement = type === 'followers' ? followersListElem : followingListElem;

        if (users.length === 0) {
            targetElement.innerHTML = '<p class="empty-message">목록이 비어있습니다.</p>';
            return;
        }

        // 현재 사용자의 팔로잉 목록 가져오기
        const currentUserFollowings = await getCurrentUserFollowings();
        
        targetElement.innerHTML = users.map(user => {
            const isFollowing = currentUserFollowings.some(following => following.id === user.id);
            return `
                <li class="user-list-item">
                    <div class="user-container">
                        <div class="user-info">
                            <img src="${user.profileImage || 'https://via.placeholder.com/40'}" 
                                 alt="사용자 프로필" 
                                 class="user-profile-image">
                            <span class="user-name">${user.nickname}</span>
                        </div>
                        <button class="follow-button ${isFollowing ? 'following' : 'not-following'}"
                                data-user-id="${user.id}">
                            ${isFollowing ? '팔로잉' : '팔로우'}
                        </button>
                    </div>
                </li>
            `;
        }).join('');

        attachFollowButtonListeners();
    } catch (error) {
        console.error(`${type} 목록 로드 중 오류 발생:`, error);
        const targetElement = type === 'followers' ? followersListElem : followingListElem;
        targetElement.innerHTML = '<p class="error-message">목록을 불러오는 중 오류가 발생했습니다.</p>';
    }
}

// 현재 로그인한 사용자의 팔로잉 목록 가져오기
async function getCurrentUserFollowings() {
    try {
        const response = await fetch('/follow/me/followings', {
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        if (!response.ok) return [];

        const result = await response.json();
        return result.data || [];
    } catch (error) {
        console.error('팔로잉 목록 조회 중 오류:', error);
        return [];
    }
}

// 팔로우 기능 관리
function attachFollowButtonListeners() {
    const followButtons = document.querySelectorAll('.follow-button');
    followButtons.forEach(button => {
        button.addEventListener('click', async () => {
            const userId = button.dataset.userId;
            const isFollowing = button.classList.contains('following');

            try {
                const response = await fetch(`/follow${isFollowing ? `/${userId}` : ''}`, {
                    method: isFollowing ? 'DELETE' : 'POST',
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: !isFollowing ? JSON.stringify({ followingId: userId }) : null
                });

                if (!response.ok) {
                    throw new Error('팔로우 처리 실패');
                }

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
document.addEventListener('DOMContentLoaded', () => {
    // DOM 요소 초기화
    followersListElem = document.getElementById('followers-list');
    followingListElem = document.getElementById('following-list');
    
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

    // 초기 데이터 로드
    loadFollowList('followers');
    loadFollowList('following');
}); 