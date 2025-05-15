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

    // 로그아웃시 로컬 스토리지에 있는 토큰 삭제
    document.getElementById("logoutBtn").addEventListener("click", () => {
        if (confirm("정말 로그아웃하시겠습니까?")) {
            const accessToken = localStorage.getItem("accessToken");

            fetch("/users/logout", {
                method: "POST",
                headers: {
                    "Authorization": `Bearer ${accessToken}`,
                    "Content-Type": "application/json"
                }
            }).finally(() => {
                localStorage.removeItem("accessToken");
                localStorage.removeItem("refreshToken");

                alert("성공적으로 로그아웃 되었습니다.");
                // 별도로 로그인 화면으로 이동시키지 않으면 이동하지 않음, 뒤로 가기 방지
                window.location.replace("/login");
            });
        }
    });
});

document.getElementById("profileBtn").addEventListener("click", () => {
    window.location.href = "/mypage.html";
});

// 로그인하면 로컬 스토리지에 accessToken, refreshToken을 저장할 수 있게 해준다.
window.onload = async () => {
    // 이미 accessToken이 저장되어 있으면 아무것도 하지 않음 (새로고침 방지)
    if (localStorage.getItem('accessToken')) {
        return;
    }

    // 새로운 로그인 이라면 토큰 값을 저장해줘야 한다.
    const urlParams = new URLSearchParams(window.location.search);
    const accessToken = urlParams.get('accessToken');
    const refreshToken = urlParams.get('refreshToken');

    if (!accessToken) {
        document.body.innerHTML = "<p>사용자 인증 정보가 없습니다.</p>";
        return;
    }

    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);

    // redirect URL 에는 accessToken, refreshToken이 쿼리 파라미터에 있어 이를 제거한다.
    window.history.replaceState({}, document.title, window.location.pathname);
};
