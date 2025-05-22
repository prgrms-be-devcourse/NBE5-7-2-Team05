let followersListElem;
let followingListElem;
let currentUserFollowings = [];

// 팔로워/팔로잉 목록 로드 함수
async function loadFollowList(type) {
    const urlParams = new URLSearchParams(window.location.search);
    const userId = urlParams.get('userId') || localStorage.getItem('userId');

    if (!userId) {
        console.error('사용자 ID를 찾을 수 없습니다.');
        return;
    }

    try {
        const response = await fetch(`/follow/${userId}/${type === 'following' ? 'followings' : 'followers'}`, {
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' }
        });

        if (!response.ok) {
            const errorText = await response.text();
            console.error(`${type} 응답:`, response.status, errorText);
            throw new Error(`${type} 목록 로드 실패`);
        }

        const result = await response.json();
        const users = result.data || [];

        const targetElement = type === 'followers' ? followersListElem : followingListElem;
        if (!targetElement) {
            console.error(`${type} 목록 엘리먼트를 찾을 수 없습니다.`);
            return;
        }

        if (users.length === 0) {
            targetElement.innerHTML = '<p class="empty-message">목록이 비어있습니다.</p>';
            return;
        }

        targetElement.innerHTML = '';
        users.forEach(user => {
            const isFollowing = currentUserFollowings.some(f => f.id === user.id);
            const isSelf = user.id.toString() === localStorage.getItem('userId');

            const li = document.createElement('li');
            li.className = 'user-list-item';
            li.innerHTML = `
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
            `;
            targetElement.appendChild(li);
        });

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
async function fetchCurrentUserFollowings() {
    const currentUserId = localStorage.getItem('userId');
    if (!currentUserId) {
        console.error('로컬 스토리지에 userId가 없습니다.');
        return [];
    }

    try {
        const response = await fetch(`/follow/${currentUserId}/followings`, {
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' }
        });

        if (!response.ok) return [];

        const result = await response.json();
        return result.data || [];
    } catch (error) {
        console.error('팔로잉 목록 조회 중 오류:', error);
        return [];
    }
}

// 팔로우 버튼 클릭 이벤트 바인딩
function attachFollowButtonListeners() {
    document.querySelectorAll('.follow-button').forEach(button => {
        button.addEventListener('click', async () => {
            const userId = button.dataset.userId;
            const isFollowing = button.classList.contains('following');

            try {
                const response = await fetch(`/follow${isFollowing ? `/${userId}` : ''}`, {
                    method: isFollowing ? 'DELETE' : 'POST',
                    headers: { 'Content-Type': 'application/json' },
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

// 프로필 클릭 시 이동
function attachProfileClickListeners() {
    document.querySelectorAll('.user-container').forEach(container => {
        container.addEventListener('click', function (e) {
            if (e.target.closest('.follow-button')) return;
            const userId = container.dataset.userId;
            if (userId) {
                window.location.href = `/user.html?userId=${userId}`;
            }
        });
    });
}

// DOMContentLoaded 시 초기화
document.addEventListener('DOMContentLoaded', async () => {
    followersListElem = document.getElementById('followers-list');
    followingListElem = document.getElementById('following-list');

    if (!followersListElem && !followingListElem) return;

    currentUserFollowings = await fetchCurrentUserFollowings();

    // 탭 버튼 바인딩
    document.querySelectorAll('.follow-nav-button').forEach(button => {
        button.addEventListener('click', () => {
            document.querySelectorAll('.follow-nav-button').forEach(btn => btn.classList.remove('active'));
            button.classList.add('active');

            const targetTab = button.dataset.tab;
            if (followersListElem) followersListElem.style.display = targetTab === 'followers' ? 'block' : 'none';
            if (followingListElem) followingListElem.style.display = targetTab === 'following' ? 'block' : 'none';
        });
    });

    await loadFollowList('followers');
    await loadFollowList('following');

    // 헤더 버튼 바인딩
    const logo = document.getElementById('homeLogo');
    if (logo) {
        logo.addEventListener('click', () => {
            const userId = localStorage.getItem('userId');
            window.location.href = userId ? `/index.html?userId=${userId}` : '/index.html';
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
        backBtn.addEventListener('click', () => {
            window.history.back();
        });
    }
});
