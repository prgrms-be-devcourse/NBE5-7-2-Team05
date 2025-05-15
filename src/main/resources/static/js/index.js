function buildCalendar(container, date = new Date()) {
    container.innerHTML = "";

    const state = {
        today: new Date(),
        current: new Date(date.getFullYear(), date.getMonth(), 1),
        selected: null,
    };

    const header = document.createElement("div");
    header.className = "calendar-header";

    const prevBtn = document.createElement("button");
    prevBtn.innerHTML = '<svg viewBox="0 0 24 24"><path d="M15.41 7.41 14 6l-6 6 6 6 1.41-1.41L10.83 12z"/></svg>';
    const nextBtn = document.createElement("button");
    nextBtn.innerHTML = '<svg viewBox="0 0 24 24"><path d="M8.59 16.59 10 18l6-6-6-6-1.41 1.41L12.17 12z"/></svg>';

    const title = document.createElement("span");
    title.className = "calendar-title";

    header.append(prevBtn, title, nextBtn);
    container.appendChild(header);

    const dayNamesEl = document.createElement("div");
    dayNamesEl.className = "day-names";
    const dayNames = ["일", "월", "화", "수", "목", "금", "토"];
    dayNames.forEach(d => {
        const dn = document.createElement("div");
        dn.textContent = d;
        dayNamesEl.appendChild(dn);
    });
    container.appendChild(dayNamesEl);

    const grid = document.createElement("div");
    grid.className = "calendar-grid";
    container.appendChild(grid);

    function render() {
        title.textContent = state.current.toLocaleString("default", {
            month: "long",
            year: "numeric",
        });

        grid.innerHTML = "";
        const firstDayIdx = state.current.getDay();
        const lastDate = new Date(state.current.getFullYear(), state.current.getMonth() + 1, 0).getDate();

        for (let i = 0; i < firstDayIdx; i++) {
            const blank = document.createElement("div");
            grid.appendChild(blank);
        }

        for (let d = 1; d <= lastDate; d++) {
            const dateEl = document.createElement("div");
            dateEl.className = "date";
            dateEl.textContent = d;

            const thisDate = new Date(state.current.getFullYear(), state.current.getMonth(), d);
            if (thisDate.toDateString() === state.today.toDateString()) {
                dateEl.classList.add("today");
            }
            if (
                state.selected &&
                thisDate.toDateString() === state.selected.toDateString()
            ) {
                dateEl.classList.add("selected");
            }

            dateEl.addEventListener("click", () => {
                state.selected = thisDate;
                render();
            });

            grid.appendChild(dateEl);
        }
    }

    prevBtn.addEventListener("click", () => {
        state.current.setMonth(state.current.getMonth() - 1);
        render();
    });
    nextBtn.addEventListener("click", () => {
        state.current.setMonth(state.current.getMonth() + 1);
        render();
    });

    render();
}


document.addEventListener("DOMContentLoaded", () => {
    const calendarContainer = document.getElementById("calendar");
    buildCalendar(calendarContainer);

    document.getElementById("logoutBtn").addEventListener("click", () => {
        if (confirm("정말 로그아웃하시겠습니까?")) {
            fetch("/users/logout", {
                method: "POST",
                credentials: "include", // ✅ 쿠키 전송
                headers: {
                    "Content-Type": "application/json"
                }
            }).finally(() => {
                alert("성공적으로 로그아웃 되었습니다.");
                window.location.replace("/login"); // 뒤로 가기 방지
            });
        }
    });
});

document.getElementById("profileBtn").addEventListener("click", () => {
    window.location.href = "/mypage";
});

window.onload = function () {
    const urlParams = new URLSearchParams(window.location.search);
    const userId = urlParams.get("userId");

    if (userId) {
        localStorage.setItem("userId", userId);
        console.log("userId 저장됨:", userId);
    } else {
        console.warn("userId가 URL에 없습니다.");
    }
};