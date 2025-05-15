document.addEventListener("DOMContentLoaded", async () => {
    // 1. 로컬 스토리지에서 accessToken 꺼내기
    const accessToken = localStorage.getItem("accessToken");

    if (!accessToken) {
        alert("로그인이 필요합니다.");
        window.location.href = "/login";
        return;
    }

    // 2. accessToken에서 userId 추출 (JWT 디코딩)
    let userId;
    try {
        const payload = JSON.parse(atob(accessToken.split('.')[1]));
        userId = payload.sub; // 실제 JWT 구조에 따라 'sub' or 'id' 등 확인 필요
    } catch (e) {
        alert("유효하지 않은 토큰입니다.");
        window.location.href = "/login";
        return;
    }

    // 3. Authorization 헤더 설정
    const authHeader = {
        headers: {
            "Authorization": `Bearer ${accessToken}`,
            "Content-Type": "application/json"
        }
    };

    // 4. 유저 프로필 정보 불러오기
    try {
        const response = await fetch(`/users/${userId}`, authHeader);
        if (!response.ok) throw new Error("유저 정보를 불러오지 못했습니다.");

        const json = await response.json();
        const data = json.data;

        document.getElementById("nickname").textContent = data.nickname;
        document.getElementById("profile-img").src = data.profileImage || "/images/default-profile.png";
        document.getElementById("intro-box").textContent = data.intro || "자기소개가 없습니다.";
    } catch (err) {
        console.error("유저 정보 로드 실패:", err);
        document.getElementById("intro-box").textContent = "정보를 불러오지 못했습니다.";
    }

    // 5. 팔로워 / 팔로잉 수 불러오기
    try {
        const [followersRes, followingsRes] = await Promise.all([
            fetch(`/follow/${userId}/followers`, authHeader),
            fetch(`/follow/${userId}/followings`, authHeader)
        ]);

        const followerJson = await followersRes.json();
        const followingJson = await followingsRes.json();

        document.getElementById("follower-count").textContent = followerJson.data.length;
        document.getElementById("following-count").textContent = followingJson.data.length;
    } catch (err) {
        console.error("팔로우 정보 로딩 실패:", err);
    }

    // 6. 할 일 목록 불러오기
    try {
        const todoRes = await fetch(`/tasks/${userId}`, authHeader);
        const todoJson = await todoRes.json();

        const todoList = document.getElementById("todo-list");
        todoList.innerHTML = ""; // 기존 항목 초기화

        todoJson.data.forEach(task => {
            const li = document.createElement("li");
            li.textContent = task.content; // 실제 필드명에 따라 수정
            todoList.appendChild(li);
        });
    } catch (err) {
        console.error("할 일 목록 로딩 실패:", err);
    }

    // 7. 프로필 수정 버튼 클릭 시 이동
    const editButton = document.querySelector("footer button");
    if (editButton) {
        editButton.onclick = () => {
            window.location.href = `/mypage/update?userId=${userId}`;
        };
    }
});
