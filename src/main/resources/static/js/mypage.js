document.addEventListener("DOMContentLoaded", async () => {
    const userId = localStorage.getItem("userId");

    // 로그인 확인
    if (!userId) {
        alert("로그인이 필요합니다.");
        window.location.href = "/loginPage";
        return;
    }

    // 홈 / 로그아웃
    document.getElementById("goHomeBtn")?.addEventListener("click", () => {
        window.location.href = "/index.html";
    });

    document.getElementById("logoutBtn")?.addEventListener("click", () => {
        localStorage.clear();
        window.location.href = "/login";
    });

    // 유저 정보 불러오기
    try {
        const res = await fetch(`/users/${userId}`, {
            method: "GET",
            credentials: "include"
        });

        const json = await res.json();
        const user = json.data;
        if (!res.ok || !user) throw new Error("유저 정보 없음");

        document.getElementById("nickname").textContent = user.nickname || "";
        document.getElementById("intro-box").textContent = user.intro || "자기소개가 없습니다.";
        document.getElementById("profile-img").src = user.profileImage || "/images/default-profile.png";
    } catch (err) {
        console.error("유저 정보 실패:", err);
        alert("유저 정보를 불러올 수 없습니다.");
    }

    // 팔로우 수 불러오기
    try {
        const res = await fetch(`/follow/${userId}`, {
            credentials: "include"
        });

        const json = await res.json();
        const data = json.data;

        document.getElementById("follower-count").textContent = data.followerCount || "0";
        document.getElementById("following-count").textContent = data.followingCount || "0";
    } catch (err) {
        console.error("팔로우 정보 실패:", err);
    }

    // 오늘의 할 일 목록 불러오기
    try {
        const today = new Date().toISOString().split("T")[0];
        const res = await fetch(`/users/${userId}/tasks?date=${today}`, {
            credentials: "include"
        });

        const json = await res.json();
        const tasks = json.data || [];

        const todoList = document.getElementById("todo-list");
        todoList.innerHTML = "";

        if (tasks.length === 0) {
            const p = document.createElement("p");
            p.textContent = "오늘의 할 일이 없습니다!";
            p.style.color = "#666";
            p.style.fontStyle = "italic";
            todoList.appendChild(p);
        } else {
            tasks.forEach(task => {
                const li = document.createElement("li");
                li.className = "custom-todo";

                const checkbox = document.createElement("input");
                checkbox.type = "checkbox";
                checkbox.checked = (task.status === "COMPLETE");
                checkbox.disabled = true;

                const span = document.createElement("span");
                span.textContent = task.content;

                if (task.status === "COMPLETE") {
                    span.style.textDecoration = "line-through";
                    span.style.color = "#888";
                }

                li.appendChild(checkbox);
                li.appendChild(span);
                todoList.appendChild(li);
            });
        }
    } catch (err) {
        console.error("할 일 목록 실패:", err);
    }

    // 이번 달 이미지 불러오기 (모달 포함)
    try {
        const res = await fetch(`/tasks/${userId}/images`, {
            credentials: "include"
        });

        const json = await res.json();
        const taskImages = json.data || [];

        const now = new Date();
        const thisMonth = now.getMonth();
        const thisYear = now.getFullYear();

        const memories = taskImages
            .filter(task => {
                const date = new Date(task.dueDate);
                return task.status === "COMPLETE" &&
                    task.imageUrl &&
                    date.getMonth() === thisMonth &&
                    date.getFullYear() === thisYear;
            })
            .sort((a, b) => new Date(b.dueDate) - new Date(a.dueDate))
            .slice(0, 4);

        const gallery = document.getElementById("memory-gallery");
        gallery.innerHTML = "";

        if (memories.length === 0) {
            gallery.innerHTML = "<p>이번 달 추억이 아직 없어요 😊</p>";
        } else {
            memories.forEach(task => {
                const img = document.createElement("img");
                img.src = task.imageUrl;
                img.className = "memory-img";


                img.addEventListener("click", () => {
                    document.getElementById("modal-img").src = task.imageUrl;
                    document.getElementById("modal-date").textContent = task.dueDate?.split("T")[0];
                    document.getElementById("modal-status").textContent = task.status;
                    document.getElementById("task-modal").classList.remove("hidden");
                });

                gallery.appendChild(img);
            });

            // 모달 닫기
            document.querySelector(".close-btn").addEventListener("click", () => {
                document.getElementById("task-modal").classList.add("hidden");
            });

            // 배경 클릭 시 닫기
            document.getElementById("task-modal").addEventListener("click", (e) => {
                if (e.target.id === "task-modal") {
                    document.getElementById("task-modal").classList.add("hidden");
                }
            });
        }
    } catch (err) {
        console.error("이미지 로딩 실패:", err);
    }

    // 프로필 수정 버튼
    const editButton = document.querySelector("footer button");
    if (editButton) {
        editButton.onclick = () => {
            window.location.href = `/mypage/update`;
        };
    }

    // 팔로우/팔로잉 링크 이동
    document.getElementById("follower-link")?.addEventListener("click", () => {
        window.location.href = `/follow-list.html?userId=${userId}&type=followers`;
    });

    document.getElementById("following-link")?.addEventListener("click", () => {
        window.location.href = `/follow-list.html?userId=${userId}&type=followings`;
    });
});
