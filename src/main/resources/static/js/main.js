let userId = 0;
window.onload = function () {
    const urlParams = new URLSearchParams(window.location.search);
    const uid = urlParams.get("userId");   // URL에서 userId를 받아서

    if (uid) {
        localStorage.setItem("userId", uid);  // localStorage에 저장하고
        userId = uid;                         // 변수에도 할당
        console.log("userId 저장됨:", userId);
    } else {
        console.warn("userId가 URL에 없습니다.");
        // URL에 없으면 localStorage에서 userId 가져오기 시도
        const storedUserId = localStorage.getItem("userId");
        if (storedUserId) {
            userId = storedUserId;
            console.log("localStorage에서 userId 불러옴:", userId);
        }
    }
};

document.addEventListener("DOMContentLoaded", () => {

    // userId가 없으면 localStorage에서 가져오기 시도
    if (!userId) {
        const storedUserId = localStorage.getItem("userId");
        if (storedUserId) {
            userId = storedUserId;
            console.log("DOMContentLoaded에서 localStorage userId 불러옴:", userId);
        }
    }

    if (!userId) {
        alert("로그인 정보가 없습니다. 다시 로그인해주세요.");
        return;  // userId 없으면 더 진행하지 않음
    }

    const form = document.getElementById("taskForm")
    const showFormBtn = document.getElementById("showFormBtn")

    showFormBtn.addEventListener("click", () => {
        form.classList.toggle("hidden")
    })

    form.addEventListener("submit", async (e) => {
        e.preventDefault()

        const rawDateTime = document.getElementById("dueDate").value
        const dueDate = rawDateTime ? `${rawDateTime}:00` : null

        let dateObj = null;
        if (rawDateTime) {
            const dateOnlyStr = rawDateTime.split("T")[0];
            dateObj = new Date(dateOnlyStr);
        }

        const taskData = {
            category: document.getElementById("category").value,
            content: document.getElementById("content").value,
            dueDate: dueDate,
            scope: document.getElementById("scope").value,
            status: "INCOMPLETE",
        }
        try {
            const res = await authFetch(`http://localhost:8080/tasks`, {
                method: "POST",
                body: JSON.stringify(taskData),
            })

            if (!res.ok) throw new Error("할 일 추가 실패")

            await fetchAndRenderTasks(dateObj)
            form.reset()
            form.classList.add("hidden")
        } catch (err) {
            alert(err.message)
        }
    })

    //fetchAndRenderTasks(selectedDate) // 달력에서 날짜 클릭 시 할일 조회해서 랜더링해주면 있을필요 없음.
})

// JSON 요청에 사용
function authFetch(url, options = {}) {
    const token = getCookie("accessToken")
    return fetch(url, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
            ...(options.headers || {})
        },
        credentials: 'include', // 쿠키 필요 시
    })
}

function getCookie(name) {
    const cookieArr = document.cookie.split(';')
    for (let i = 0; i < cookieArr.length; i++) {
        const cookiePair = cookieArr[i].trim()
        if (cookiePair.startsWith(name + '=')) {
            return cookiePair.substring(name.length + 1)
        }
    }
    return null
}

// 파일 업로드 요청에 사용
async function authUpload(url, formData) {
    return fetch(url, {
        method: "PATCH",
        headers: {},
        body: formData,
        credentials : "include",
    })
}

export async function fetchAndRenderTasks(date) {
    try {
        const dateStr = date.getFullYear() + "-" +
            String(date.getMonth() + 1).padStart(2, '0') + "-" +
            String(date.getDate()).padStart(2, '0');

        const url = `/users/${userId}/tasks?date=${dateStr}`;


        const res = await authFetch(url);
        const data = await res.json();
        console.log(data.data);
        renderTasksByCategory(data.data || []);
    } catch (err) {
        console.error('할 일 조회 실패:', err);
    }
}

function renderTasksByCategory(tasks) {
    const container = document.getElementById("task-list");
    container.innerHTML = "";

    if (!tasks || tasks.length === 0) {
        container.innerHTML = '<p class="text-gray-500 text-center mt-4">등록된 할 일이 없습니다.</p>';
        return;
    }

    // 카테고리별로 그룹화
    const grouped = {};
    tasks.forEach((task) => {
        if (!grouped[task.category]) grouped[task.category] = [];
        grouped[task.category].push(task);
    });

    // 전체를 감쌀 컨테이너 (좌우 배치용 flex)
    const categoryWrapper = document.createElement("div");
    categoryWrapper.className = "flex gap-6 overflow-x-auto px-4"; // tailwind 스타일, 필요시 수정

    Object.entries(grouped).forEach(([category, categoryTasks]) => {
        const column = document.createElement("div");
        column.className = "min-w-[250px] flex-shrink-0 bg-gray-100 p-3 rounded shadow-md";

        const categoryHeader = document.createElement("h3");
        categoryHeader.className = "font-semibold text-lg mb-2 border-b pb-1";
        categoryHeader.textContent = category;

        column.appendChild(categoryHeader);

        categoryTasks.forEach((task) => {
            const taskItem = createTaskItem(task);
            column.appendChild(taskItem);
        });

        categoryWrapper.appendChild(column);
    });

    container.appendChild(categoryWrapper);
}

function dueDateToDate(dueDateStr) {
    if (!dueDateStr) return null;  // null이나 undefined 처리
    return new Date(dueDateStr);
}

function createTaskItem(task) {
    const taskItem = document.createElement("div")
    taskItem.className = "task-item"
    taskItem.id = `task-${task.id}`
    taskItem.setAttribute("data-category", task.category);
    taskItem.setAttribute("data-scope", task.scope);

    // 체크박스
    const checkbox = document.createElement("div")
    checkbox.className = `task-checkbox ${task.status === "COMPLETE" ? "checked" : ""}`
    checkbox.innerHTML =
        task.status === "COMPLETE"
            ? '<svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M5 12L10 17L19 8" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>'
            : ""
    checkbox.addEventListener("click", () => {
        const isChecked = !checkbox.classList.contains("checked")
        toggleTaskStatus(task.id, isChecked)
    })

    // 할 일 내용
    const content = document.createElement("div")
    content.className = "task-content"

    const title = document.createElement("p")
    title.className = `task-title ${task.status === "COMPLETE" ? "completed" : ""}`
    title.textContent = task.content

    content.appendChild(title)

    // 메뉴 버튼
    const menu = document.createElement("div")
    menu.className = "task-menu"

    const menuIcon = document.createElement("div")
    menuIcon.className = "task-menu-icon"
    menuIcon.innerHTML =
        '<svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M12 13C12.5523 13 13 12.5523 13 12C13 11.4477 12.5523 11 12 11C11.4477 11 11 11.4477 11 12C11 12.5523 11.4477 13 12 13Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/><path d="M19 13C19.5523 13 20 12.5523 20 12C20 11.4477 19.5523 11 19 11C18.4477 11 18 11.4477 18 12C18 12.5523 18.4477 13 19 13Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/><path d="M5 13C5.55228 13 6 12.5523 6 12C6 11.4477 5.55228 11 5 11C4.44772 11 4 11.4477 4 12C4 12.5523 4.44772 13 5 13Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>'

    const menuDropdown = document.createElement("div")
    menuDropdown.className = "task-menu-dropdown hidden"

    const menuItems = [
        { text: "할 일 수정", action: () => editTask(task.id) },
        { text: "할 일 삭제", action: () => deleteTask(task.id) },
        { text: "인증 사진 업로드", action: () => uploadImage(task.id) },
    ]

    menuItems.forEach((item) => {
        const menuItem = document.createElement("div")
        menuItem.className = "task-menu-item"
        menuItem.textContent = item.text
        menuItem.addEventListener("click", (e) => {
            e.stopPropagation()
            item.action()
            menuDropdown.classList.add("hidden")
        })
        menuDropdown.appendChild(menuItem)
    })

    menuIcon.addEventListener("click", (e) => {
        e.stopPropagation()
        menuDropdown.classList.toggle("hidden")
    })

    // 다른 곳 클릭 시 메뉴 닫기
    document.addEventListener("click", () => {
        menuDropdown.classList.add("hidden")
    })

    menu.appendChild(menuIcon)
    menu.appendChild(menuDropdown)

    // 댓글 푸터 요소
    const footer = document.createElement("div")
    footer.className = "task-footer"

    const commentIcon = document.createElement("span")
    commentIcon.className = "comment-icon"
    commentIcon.innerHTML = "💬"

    const commentCount = document.createElement("span")
    commentCount.className = "comment-count"
    commentCount.textContent = task.commentCount || 0

    footer.appendChild(commentIcon)
    footer.appendChild(commentCount)

    // 요소 추가
    taskItem.appendChild(checkbox)
    taskItem.appendChild(content)
    taskItem.appendChild(menu)

    // 이미지가 있는 경우 추가
    if (task.taskImage) {
        const imageContainer = document.createElement("div")
        imageContainer.className = "task-image"
        imageContainer.style.marginTop = "8px" // 여백 추가

        const img = document.createElement("img")
        img.src = task.taskImage
        img.alt = "Task image"
        img.style.maxWidth = "150px"
        img.style.maxHeight = "150px"
        img.style.width = "100%"
        img.style.height = "auto"
        img.style.borderRadius = "8px"
        img.style.objectFit = "cover"
        img.style.maxHeight = "200px"

        imageContainer.appendChild(img)

        // 텍스트(content) 밑에 이미지 삽입
        content.appendChild(imageContainer)
    }
    taskItem.appendChild(footer)
    return taskItem
}

async function toggleTaskStatus(taskId, isChecked) {

    let res = await authFetch(`http://localhost:8080/tasks/${taskId}`);
    if (!res.ok) {
        alert("할 일 정보를 불러오는 데 실패했습니다.");
        return;
    }
    let json = await res.json();
    let task = json.data;
    if (!task) {
        alert("할 일 정보를 찾을 수 없습니다.");
        return;
    }
    if (task.taskImage) {
        alert("인증 이미지가 있는 할 일은 미완료 상태일 수 없습니다.");
        return;
    }

    const status = isChecked ? "COMPLETE" : "INCOMPLETE"
    const checkbox = document.querySelector(`#task-${taskId} .task-checkbox`)
    const title = document.querySelector(`#task-${taskId} .task-title`)

    if (isChecked) {
        checkbox.classList.add("checked")
        checkbox.innerHTML =
            '<svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M5 12L10 17L19 8" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>'
        title.classList.add("completed")
    } else {
        checkbox.classList.remove("checked")
        checkbox.innerHTML = ""
        title.classList.remove("completed")
    }

    try {
        const res = await authFetch(`http://localhost:8080/tasks/${taskId}/status`, {
            method: "PATCH",
            body: JSON.stringify({ status }),
        })

        if (!res.ok) throw new Error("상태 업데이트 실패")
    } catch (err) {
        console.error("상태 업데이트 실패:", err)
        // 실패 시 원래 상태로 되돌림
        await fetchAndRenderTasks(dueDateToDate(task.dueDate))
    }
}

async function editTask(taskId) {
    const taskItem = document.getElementById(`task-${taskId}`);
    if (!taskItem) return;

    // 현재 할 일 정보 가져오기
    const taskContent = taskItem.querySelector('.task-content');
    const taskTitle = taskItem.querySelector('.task-title');
    const originalContent = taskTitle.textContent.trim();


    const originalCategory = taskItem.getAttribute('data-category') || '';
    const originalScope = taskItem.getAttribute('data-scope') || 'PRIVATE';

    // 기존 할 일 내용 숨기기
    taskItem.style.display = 'none';

    // 수정 폼 생성
    const form = document.createElement('form');
    form.className = "edit-form bg-gray-200 p-4 rounded-lg mb-3";
    form.innerHTML = `
    <div class="flex flex-col gap-2">
      <input type="text" name="category" placeholder="카테고리" class="input input-bordered w-full" value="${originalCategory}" required />
      <input type="text" name="content" placeholder="할 일 내용" class="input input-bordered w-full" value="${originalContent}" required />
      <select name="scope" class="select select-bordered w-full" required>
        <option value="PRIVATE" ${originalScope === 'PRIVATE' ? 'selected' : ''}>PRIVATE</option>
        <option value="PUBLIC" ${originalScope === 'PUBLIC' ? 'selected' : ''}>PUBLIC</option>
        <option value="FOLLOWERS" ${originalScope === 'FOLLOWERS' ? 'selected' : ''}>FOLLOWERS</option>
      </select>
      <div class="flex gap-2 justify-end mt-2">
        <button type="submit" class="btn btn-sm btn-primary">저장</button>
        <button type="button" class="btn btn-sm btn-ghost cancel-edit">취소</button>
      </div>
    </div>
  `;

    // 폼 제출 이벤트 처리
    form.addEventListener('submit', async function(e) {
        e.preventDefault();

        const updatedCategory = form.category.value.trim();
        const updatedContent = form.content.value.trim();
        const updatedScope = form.scope.value;

        try {
            const response = await authFetch(`http://localhost:8080/tasks/${taskId}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    category: updatedCategory,
                    content: updatedContent,
                    scope: updatedScope
                })
            });

            if (!response.ok) throw new Error('할 일 수정에 실패했습니다.');

            // 수정 성공 시 UI 업데이트
            form.remove();
            taskItem.style.display = '';

            // 최신 할 일 목록 다시 불러오기
            let res = await authFetch(`http://localhost:8080/tasks/${taskId}`);
            if (!res.ok) {
                alert("할 일 정보를 불러오는 데 실패했습니다.");
                return;
            }
            let json = await res.json();
            let task = json.data;
            await fetchAndRenderTasks(dueDateToDate(task.dueDate))

        } catch (err) {
            console.error('수정 실패:', err);
            alert(err.message);
        }
    });

    // 취소 버튼 이벤트 처리
    form.querySelector('.cancel-edit').addEventListener('click', function() {
        form.remove();
        taskItem.style.display = '';
    });

    // 폼을 할 일 항목 바로 뒤에 삽입
    taskItem.parentNode.insertBefore(form, taskItem.nextSibling);
}


async function deleteTask(taskId) {

    let res = await authFetch(`http://localhost:8080/tasks/${taskId}`);
    if (!res.ok) {
        alert("할 일 정보를 불러오는 데 실패했습니다.");
        return;
    }
    let json = await res.json();
    let task = json.data;
    if (!task) {
        alert("할 일 정보를 찾을 수 없습니다.");
        return;
    }

    if (task.taskImage) {
        alert("인증 이미지가 있는 할 일은 삭제할 수 없습니다.");
        return;
    }

    const confirmed = confirm("정말 삭제하시겠습니까?")
    if (!confirmed) return

    try {
        const res = await authFetch(`http://localhost:8080/tasks/${taskId}`, {
            method: "DELETE",
        })

        if (!res.ok) throw new Error("삭제 실패")

        await fetchAndRenderTasks(dueDateToDate(task.dueDate))
    } catch (err) {
        console.error("삭제 실패:", err)
        alert(err.message)
    }
}

async function uploadImage(taskId) {
    // 먼저 해당 task의 최신 상태를 가져옴
    let res = await authFetch(`http://localhost:8080/tasks/${taskId}`);
    if (!res.ok) {
        alert("할 일 정보를 불러오는 데 실패했습니다.");
        return;
    }

    let json = await res.json();
    let task = json.data;
    if (!task) {
        alert("할 일 정보를 찾을 수 없습니다.");
        return;
    }
    if (task.status !== "COMPLETE") {
        alert("할 일이 완료된 상태에서만 인증 사진을 업로드할 수 있습니다.");
        return;
    }

    const input = document.createElement("input");
    input.type = "file";
    input.accept = "image/*";

    input.addEventListener("change", async () => {
        const file = input.files[0];
        if (!file) return;

        const formData = new FormData();
        formData.append("image", file);

        try {
            const uploadRes = await authUpload(`http://localhost:8080/tasks/${taskId}/image`, formData);
            if (!uploadRes.ok) throw new Error("이미지 업로드 실패");

            await fetchAndRenderTasks(dueDateToDate(task.dueDate))
        } catch (err) {
            console.error("이미지 업로드 실패:", err);
            alert(err.message);
        }
    });

    document.body.appendChild(input);
    input.click();
    document.body.removeChild(input);
}
