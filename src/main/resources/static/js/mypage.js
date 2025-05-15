document.addEventListener("DOMContentLoaded", async () => {
    let userId = null;

    // 백엔드에서 인증된 유저 정보 요청 (accessToken은 HttpOnly 쿠키로 자동 전송됨)
    try {
        const res = await fetch("/users/{userId}", {
            method: "GET",
            credentials: "include", // ✅ 쿠키 자동 포함
        });

        if (!res.ok) throw new Error("유저 정보 요청 실패");

        const { result } = await res.json();
        userId = result.id; // ✅ 서버에서 userId 포함해서 보내줘야 함

        document.getElementById("nickname").textContent = result.nickname;
        document.getElementById("intro-box").textContent = result.intro || "자기소개가 없습니다.";
        document.getElementById("profile-img").src = result.profileImage || "/images/default-profile.png";
    } catch (err) {
        console.error("유저 정보 로딩 실패:", err);
        alert("로그인이 필요합니다.");
        window.location.href = "/login";
        return;
    }

    // 팔로워 & 팔로잉 정보 요청
    try {
        const [followersRes, followingsRes] = await Promise.all([
            fetch(`/follow/${userId}/followers`, { credentials: "include" }),
            fetch(`/follow/${userId}/followings`, { credentials: "include" })
        ]);

        const followerData = await followersRes.json();
        const followingData = await followingsRes.json();

        document.getElementById("follower-count").textContent = followerData.result.length;
        document.getElementById("following-count").textContent = followingData.result.length;
    } catch (err) {
        console.error("팔로우 정보 로딩 실패:", err);
    }

    // TODO 목록 요청
    try {
        const todoRes = await fetch(`/tasks/${userId}`, { credentials: "include" });
        const todoData = await todoRes.json();

        const todoList = document.getElementById("todo-list");
        todoList.innerHTML = "";

        todoData.result.forEach(task => {
            const li = document.createElement("li");
            li.textContent = task.content;
            todoList.appendChild(li);
        });
    } catch (err) {
        console.error("할 일 목록 로딩 실패:", err);
    }

    // 프로필 수정 이동 버튼
    const editButton = document.querySelector("footer button");
    if (editButton) {
        editButton.onclick = () => {
            window.location.href = `/mypage/update`;
        };
    }
});
