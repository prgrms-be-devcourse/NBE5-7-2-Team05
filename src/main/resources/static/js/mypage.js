document.addEventListener("DOMContentLoaded", async () => {
    const userId = localStorage.getItem("userId");
    console.log("userId:", userId);

    // ✅ 로그인 확인
    if (!userId) {
        alert("로그인이 필요합니다.");
        window.location.href = "/loginPage";
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

    // ✅ 팔로우 정보 불러오기 (단일 요청)
    try {
        const res = await fetch(`/follow/${userId}`, {
            credentials: "include",
            headers: { "Content-Type": "application/json" }
        });

        if (!res.ok) throw new Error("응답 실패");

        const data = await res.json();
        const result = data.data;

        console.log("팔로우 정보 !!!! : ", result);

        document.getElementById("follower-count").textContent = result.followerCount || 0;
        document.getElementById("following-count").textContent = result.followingCount || 0;
    } catch (err) {
        console.error("팔로우 정보 로딩 실패:", err);
        document.getElementById("follower-count").textContent = "0";
        document.getElementById("following-count").textContent = "0";
    }

    // ✅ 오늘의 TODO 불러오기
    try {
        const today = new Date().toISOString().split("T")[0];

        const todoRes = await fetch(`/users/${userId}/tasks?date=${today}`, {
            credentials: "include",
            headers: {
                "Content-Type": "application/json"
            }
        });

        const todoData = await todoRes.json();
        const tasks = todoData.data || [];

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
                li.className = "custom-todo";

                const checkbox = document.createElement("input");
                checkbox.type = "checkbox";
                checkbox.checked = (task.status === "COMPLETE");
                checkbox.disabled = true;

                const text = document.createElement("span");
                text.textContent = ` ${task.content}`;

                if (task.status === "COMPLETE") {
                    text.style.textDecoration = "line-through";
                    text.style.color = "#888";
                }

                li.appendChild(checkbox);
                li.appendChild(text);
                todoList.appendChild(li);
            });
        }
    } catch (err) {
        console.error("할 일 목록 로딩 실패:", err);
    }

    // ✅ This Month's Memories 불러오기
    try {
        const imageRes = await fetch(`/tasks/${userId}/images`, {
            credentials: "include"
        });

        const imageData = await imageRes.json();
        const taskImages = imageData.data || [];

        console.log("📦 전체 taskImages:", taskImages);
        taskImages.forEach(task => {
            console.log({
                status: task.status,
                imageUrl: task.imageUrl,
                dueDate: task.dueDate
            });
        });

        const now = new Date();
        const thisMonth = now.getMonth();
        const thisYear = now.getFullYear();

        const memories = taskImages
            .filter(task => {
                const date = new Date(task.dueDate);
                const isComplete = task.status?.toUpperCase() === "COMPLETE";
                const hasImage = !!task.imageUrl;
                const isThisMonth = date.getMonth() === thisMonth && date.getFullYear() === thisYear;
                return isComplete && hasImage && isThisMonth;
            })
            .sort((a, b) => new Date(b.dueDate) - new Date(a.dueDate))
            .reverse()
            .slice(0, 4);

        console.log("🎉 이번 달 추억:", memories);

        const gallery = document.getElementById("memory-gallery");
        gallery.innerHTML = "";

        if (memories.length === 0) {
            gallery.innerHTML = "<p>이번 달 추억이 아직 없어요 😊</p>";
        } else {
            memories.forEach(task => {
                const img = document.createElement("img");
                img.src = task.imageUrl;
                img.alt = task.content;
                img.className = "memory-img";

                img.addEventListener("click", () => {
                    window.open(task.imageUrl, "_blank");
                });

                gallery.appendChild(img);
            });
        }
    } catch (err) {
        console.error("This Month's Memories 불러오기 실패:", err);
    }

    // ✅ 프로필 수정 버튼 클릭 → 이동
    const editButton = document.querySelector("footer button");
    if (editButton) {
        editButton.onclick = () => {
            window.location.href = `/mypage/update`;
        };
    }
});
