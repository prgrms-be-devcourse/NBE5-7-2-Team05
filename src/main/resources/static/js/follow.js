// 전역 변수 선언
let followersListElem;
let followingListElem;

// 팔로워/팔로잉 목록 로드 함수
async function loadFollowList(type) {
    // URL에서 사용자 ID 가져오기
    const urlParams = new URLSearchParams(window.location.search);
    const userId = urlParams.get('userId') || localStorage.getItem('userId');
    
    if (!userId) {
        console.error('사용자 ID를 찾을 수 없습니다.');
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

        if (!targetElement) {
            console.error(`${type} 목록 엘리먼트를 찾을 수 없습니다.`);
            return;
        }

        if (users.length === 0) {
            targetElement.innerHTML = '<p class="empty-message">목록이 비어있습니다.</p>';
            return;
        }

        // 현재 사용자의 팔로잉 목록 가져오기
        const currentUserFollowings = await getCurrentUserFollowings();
        
        targetElement.innerHTML = users.map(user => {
            const isFollowing = currentUserFollowings.some(following => following.id === user.id);
            const isSelf = user.id.toString() === localStorage.getItem('userId');
            return `
                <li class="user-list-item">
                    <div class="user-container" data-user-id="${user.id}">
                        <div class="user-info">
                            <img src="${user.profileImage || '/images/default-profile.png'}" 
                                 alt="사용자 프로필" 
                                 class="user-profile-image"
                                 onerror="this.src='/images/default-profile.png'">
                            <div class="user-details">
                                <span class="user-name">${user.nickname}</span>
                                ${user.intro ? `<p class="user-intro">${user.intro}</p>` : ''}
                            </div>
                        </div>
                        ${!isSelf ? `
                            <button class="follow-button ${isFollowing ? 'following' : 'not-following'}"
                                    data-user-id="${user.id}">
                                ${isFollowing ? '팔로잉' : '팔로우'}
                            </button>
                        ` : ''}
                    </div>
                </li>
            `;
        }).join('');

        attachFollowButtonListeners();
        attachProfileClickListeners();
    } catch (error) {
        console.error(`${type} 목록 로드 중 오류 발생:`, error);
        const targetElement = type === 'followers' ? followersListElem : followingListElem;
        if (targetElement) {
            targetElement.innerHTML = '<p class="error-message">목록을 불러오는 중 오류가 발생했습니다.</p>';
        }
    }
}

// 현재 로그인한 사용자의 팔로잉 목록 가져오기
async function getCurrentUserFollowings() {
    try {
        const currentUserId = localStorage.getItem('userId');
        if (!currentUserId) {
            console.error('로컬 스토리지에 userId가 없습니다.');
            return [];
        }

        const response = await fetch(`/follow/${currentUserId}/followings`, {
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
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    credentials: 'include',
                    body: !isFollowing ? JSON.stringify({ followingId: parseInt(userId) }) : null
                });

                if (!response.ok) {
                    const errorData = await response.json();
                    throw new Error(errorData.message || '팔로우 처리 실패');
                }

                button.classList.toggle('following');
                button.classList.toggle('not-following');
                button.textContent = isFollowing ? '팔로우' : '팔로잉';
            } catch (error) {
                console.error('팔로우 처리 중 오류 발생:', error);
                alert(error.message || '팔로우 처리 중 오류가 발생했습니다.');
            }
        });
    });
}

// 프로필 클릭 시 해당 유저의 할 일 목록으로 이동
function attachProfileClickListeners() {
    const containers = document.querySelectorAll('.user-container');
    containers.forEach(container => {
        container.addEventListener('click', function(e) {
            // 팔로우 버튼 클릭 시는 무시
            if (e.target.closest('.follow-button')) return;
            const userId = container.dataset.userId;
            if (userId) {
                window.location.href = `/user.html?userId=${userId}`;
            }
        });
    });
}

// 팔로우 목록 관리
document.addEventListener('DOMContentLoaded', () => {
    // DOM 요소 초기화
    followersListElem = document.getElementById('followers-list');
    followingListElem = document.getElementById('following-list');
    
    if (followersListElem || followingListElem) {
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
                if (followersListElem) followersListElem.style.display = targetTab === 'followers' ? 'block' : 'none';
                if (followingListElem) followingListElem.style.display = targetTab === 'following' ? 'block' : 'none';
            });
        });

        // 초기 데이터 로드
        loadFollowList('followers');
        loadFollowList('following');
    }

    // ===== Header actions =====
    const logo = document.getElementById('homeLogo');
    if (logo) {
        logo.addEventListener('click', () => {
            window.location.href = '/index.html';
        });
    }
    const profileBtn = document.getElementById('profileBtn');
    if (profileBtn) {
        profileBtn.addEventListener('click', () => {
            window.location.href = '/mypage';
        });
    }
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', () => {
            if (typeof AUTH !== 'undefined' && AUTH.logout) {
                AUTH.logout();
            } else {
                window.location.replace('/loginPage');
            }
        });
    }

    const backBtn = document.getElementById('backBtn');
    if (backBtn) {
        backBtn.addEventListener('click', function() {
            window.history.back();
        });
    }
}); 