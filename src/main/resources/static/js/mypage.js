document.addEventListener("DOMContentLoaded", async () => {
    const userId = localStorage.getItem("userId");

    console.log("userId:", userId);

    // ✅ 로그인 확인
    if (!userId) {
        alert("로그인이 필요합니다.");
        window.location.href = "/login";
        return;
    }

    // ✅ 홈 링크 설정
    const homeLink = document.getElementById("home-link");
    if (homeLink) {
        homeLink.href = `/index.html?userId=${userId}`;
    }

    // ✅ 프로필 정보 불러오기
    try {
        const response = await fetch(`/users/${userId}`, {
            method: "GET",
            credentials: "include",
        });

        if (!response.ok) throw new Error("유저 정보 불러오기 실패");

        const result = await response.json();
        const data = result.data;

        document.getElementById("nickname").textContent = data.nickname;
        document.getElementById("intro-box").textContent = data.intro || "자기소개가 없습니다.";
        document.getElementById("profile-img").src = data.profileImage || "/images/default-profile.png";
    } catch (err) {
        console.error("유저 정보 로딩 실패:", err);
        document.getElementById("intro-box").textContent = "정보를 불러오지 못했습니다.";
    }

    // ✅ 팔로우 정보 불러오기
    try {
        const [followersRes, followingsRes] = await Promise.all([
            fetch(`/follow/${userId}/followers`, {
                credentials: "include",
                headers: { "Content-Type": "application/json" }
            }),
            fetch(`/follow/${userId}/followings`, {
                credentials: "include",
                headers: { "Content-Type": "application/json" }
            })
        ]);

        const followerData = await followersRes.json();
        const followingData = await followingsRes.json();

        const followers = followerData.result || [];
        const followings = followingData.result || [];

        document.getElementById("follower-count").textContent = followers.length;
        document.getElementById("following-count").textContent = followings.length;
    } catch (err) {
        console.error("팔로우 정보 로딩 실패:", err);
        document.getElementById("follower-count").textContent = "0";
        document.getElementById("following-count").textContent = "0";
    }

    // ✅ TODO 목록 불러오기
    try {
        const today = new Date().toISOString().split("T")[0];  // ✅ "2025-05-19" 형식

        const todoRes = await fetch(`/users/${userId}/tasks?date=${today}`, {
            credentials: "include",
            headers: {
                "Content-Type": "application/json"
            }
        });

        const todoData = await todoRes.json();
        const tasks = todoData.data || [];

        console.log(tasks);

        const todoList = document.getElementById("todo-list");
        todoList.innerHTML = "";

        if (tasks.length === 0) {
            const emptyMsg = document.createElement("p");
            emptyMsg.textContent = "오늘의 할 일이 없습니다! 오늘도 계획을 짜서 힘찬 하루를 시작해보는 것은 어떤가요?";
            emptyMsg.style.color = "#666";
            emptyMsg.style.fontStyle = "italic";
            todoList.appendChild(emptyMsg);
        } else {
            tasks.forEach(task => {
                const li = document.createElement("li");

                const checkbox = document.createElement("input");
                checkbox.type = "checkbox";
                checkbox.disabled = true;
                checkbox.checked = task.status === "COMPLETE";

                const text = document.createTextNode(` ${task.content}`);
                li.appendChild(checkbox);
                li.appendChild(text);

                todoList.appendChild(li);
            });
        }
    } catch (err) {
        console.error("할 일 목록 로딩 실패:", err);
    }

    // ✅ 프로필 수정 버튼 클릭 → 이동
    const editButton = document.querySelector("footer button");
    if (editButton) {
        editButton.onclick = () => {
            window.location.href = `/mypage/update`;
        };
    }
});
