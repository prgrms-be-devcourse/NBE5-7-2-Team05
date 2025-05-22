import {apiFetch} from "./token-reissue.js";
import {buildCalendar} from "./index.js";

// Fetch logged-in user's userId from server
async function setUserIdFromServer() {
    try {
        const res = await apiFetch('/users/me');
        if (!res.ok) throw new Error("인증 필요");
        const response = await res.json();
        localStorage.setItem("userId", response.data);
        console.log("✅ userId 저장됨:", response.data);
    } catch (e) {
        console.error("로그인 필요:", e);
        alert("로그인이 필요합니다.");
        window.location.href = "/login";
    }
}
setUserIdFromServer();

document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("taskForm")
    const showFormBtn = document.getElementById("showFormBtn")

    showFormBtn.addEventListener("click", () => {
        form.classList.toggle("hidden")
    })

    form.addEventListener("submit", async (e) => {
        e.preventDefault()

        const rawDateTime = document.getElementById("dueDate").value
        const dueDate = rawDateTime ? `${rawDateTime}:00` : null

        const taskData = {
            category: document.getElementById("category").value,
            content: document.getElementById("content").value,
            dueDate: dueDate,
            scope: document.getElementById("scope").value,
            status: "INCOMPLETE",
        }
        try {
            const res = await authFetch(`/tasks`, {
                method: "POST",
                body: JSON.stringify(taskData),
            })
            if (!res.ok) throw new Error("마감일을 지금보다 이후 시간으로 설정해주세요.")

            await fetchAndRenderTasks(new Date(), localStorage.getItem("userId"))
            const calendarEl = document.getElementById("calendar");
            if (calendarEl) {
                buildCalendar(calendarEl, localStorage.getItem("userId"));
            } else {
                console.warn("❗ calendar 요소가 없음");
            }
            form.reset()
            form.classList.add("hidden")
        } catch (err) {
            alert(err.message)
        }
    })
    // 댓글 기능 추가
    initCommentFeature()
})

// JSON 요청에 사용
export function authFetch(url, options = {}) {
    const token = getCookie("accessToken")
    return fetch(url, {
        ...options,
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
            ...(options.headers || {}),
        },
        credentials: "include", // 쿠키 필요 시
    })
}

function getCookie(name) {
    const cookieArr = document.cookie.split(";")
    for (let i = 0; i < cookieArr.length; i++) {
        const cookiePair = cookieArr[i].trim()
        if (cookiePair.startsWith(name + "=")) {
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
        credentials: "include",
    })
}

export async function fetchAndRenderTasks(date, targetUserId) {
    try {
        const dateStr =
            date.getFullYear() +
            "-" +
            String(date.getMonth() + 1).padStart(2, "0") +
            "-" +
            String(date.getDate()).padStart(2, "0")

        const url = `/users/${targetUserId}/tasks?date=${dateStr}`

        const res = await apiFetch(url)
        const data = await res.json()
        renderTasksByCategory(data.data || [], targetUserId)
    } catch (err) {
        console.error("할 일 조회 실패:", err)
    }
}

function renderTasksByCategory(tasks, targetUserId) {
    const container = document.getElementById("task-list")
    container.innerHTML = ""

    if (!tasks || tasks.length === 0) {
        container.innerHTML = '<p class="text-gray-500 text-center mt-4">등록된 할 일이 없습니다.</p>'
        return
    }

    // 카테고리별로 그룹화
    const grouped = {}
    tasks.forEach((task) => {
        if (!grouped[task.category]) grouped[task.category] = []
        grouped[task.category].push(task)
    })

    // 전체를 감쌀 컨테이너 (그리드 레이아웃으로 변경)
    const categoryWrapper = document.createElement("div")
    categoryWrapper.className = "grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6 px-4"
    categoryWrapper.style.width = "100%" // 전체 너비 사용

    Object.entries(grouped).forEach(([category, categoryTasks]) => {
        const column = document.createElement("div")
        column.className = "bg-gray-100 p-3 rounded shadow-md"

        const categoryHeader = document.createElement("h3")
        categoryHeader.className = "font-semibold text-lg mb-2 border-b pb-1"
        categoryHeader.textContent = category

        column.appendChild(categoryHeader)

        categoryTasks.forEach((task) => {
            const taskItem = createTaskItem(task, targetUserId)
            column.appendChild(taskItem)
        })

        categoryWrapper.appendChild(column)
    })

    container.appendChild(categoryWrapper)

    setTimeout(() => {
        const taskItems = document.querySelectorAll(".task-item")
        for (const taskItem of taskItems) {
            const taskId = taskItem.id.replace("task-", "")
            refreshCommentCount(taskId)
        }
    }, 100)
}

function dueDateToDate(dueDateStr) {
    if (!dueDateStr) return null // null이나 undefined 처리
    return new Date(dueDateStr)
}

function createTaskItem(task, targetUserId) {
    const existingUserId = localStorage.getItem("userId")
    const isMine = targetUserId === existingUserId;
    console.log("targetUserId: ", targetUserId)
    console.log("existingUserId: ", existingUserId)
    console.log(isMine)

    const taskItem = document.createElement("div")
    taskItem.className = "task-item"
    taskItem.id = `task-${task.id}`
    taskItem.setAttribute("data-category", task.category)
    taskItem.setAttribute("data-scope", task.scope)

    // 체크박스
    const checkbox = document.createElement("div")
    checkbox.className = `task-checkbox ${task.status === "COMPLETE" ? "checked" : ""}`
    checkbox.innerHTML =
        task.status === "COMPLETE"
            ? '<svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M5 12L10 17L19 8" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>'
            : ""

    if (isMine) {
        checkbox.addEventListener("click", () => {
            const isChecked = !checkbox.classList.contains("checked");
            toggleTaskStatus(task.id, isChecked);
        });
    }

    // 할 일 내용
    const content = document.createElement("div")
    content.className = "task-content"

    const title = document.createElement("p")
    title.className = `task-title ${task.status === "COMPLETE" ? "completed" : ""}`
    title.textContent = task.content

    content.appendChild(title)

    // ===== 메뉴 버튼 (본인일 때만) =====
    if (isMine) {
        const menu = document.createElement("div");
        menu.className = "task-menu";

        const menuIcon = document.createElement("div");
        menuIcon.className = "task-menu-icon";
        menuIcon.innerHTML = `
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 13C12.5523 13 13 12.5523 13 12C13 11.4477 12.5523 11 12 11
                       C11.4477 11 11 11.4477 11 12C11 12.5523 11.4477 13 12 13Z"
                    stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M19 13C19.5523 13 20 12.5523 20 12
                       C20 11.4477 19.5523 11 19 11
                       C18.4477 11 18 11.4477 18 12
                       C18 12.5523 18.4477 13 19 13Z"
                    stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M5 13C5.55228 13 6 12.5523 6 12
                       C6 11.4477 5.55228 11 5 11
                       C4.44772 11 4 11.4477 4 12
                       C4 12.5523 4.44772 13 5 13Z"
                    stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>`;

        const menuDropdown = document.createElement("div");
        menuDropdown.className = "task-menu-dropdown hidden";

        const menuItems = [
            { text: "할 일 수정", action: () => editTask(task.id) },
            { text: "할 일 삭제", action: () => deleteTask(task.id) },
            { text: "인증 사진 업로드", action: () => uploadImage(task.id) },
            { text: "내일로 미루기", action: () => postponeDueDate(task.id) },
        ];

        menuItems.forEach(item => {
            const menuItem = document.createElement("div");
            menuItem.className = "task-menu-item";
            menuItem.textContent = item.text;
            menuItem.addEventListener("click", (e) => {
                e.stopPropagation();
                item.action();
                menuDropdown.classList.add("hidden");
            });
            menuDropdown.appendChild(menuItem);
        });

        menuIcon.addEventListener("click", (e) => {
            e.stopPropagation();
            menuDropdown.classList.toggle("hidden");
        });

        document.addEventListener("click", () => {
            menuDropdown.classList.add("hidden");
        });

        menu.appendChild(menuIcon);
        menu.appendChild(menuDropdown);
        taskItem.appendChild(menu);
    }

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
    const res = await authFetch(`/tasks/${taskId}`)
    if (!res.ok) {
        alert("할 일 정보를 불러오는 데 실패했습니다.")
        return
    }
    const json = await res.json()
    const task = json.data
    if (!task) {
        alert("할 일 정보를 찾을 수 없습니다.")
        return
    }
    if (task.taskImage) {
        alert("완료 인증 이미지가 있는 할 일은 미완료 상태로 변경할 수 없습니다.")
        return
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
        const res = await authFetch(`/tasks/${taskId}/status`, {
            method: "PATCH",
            body: JSON.stringify({ status }),
        })

        if (!res.ok) throw new Error("상태 업데이트 실패")
    } catch (err) {
        console.error("상태 업데이트 실패:", err)
        // 실패 시 원래 상태로 되돌림
        await fetchAndRenderTasks(dueDateToDate(task.dueDate), localStorage.getItem("userId"))
    }
}

async function editTask(taskId) {
    const taskItem = document.getElementById(`task-${taskId}`)
    if (!taskItem) return

    // 현재 할 일 정보 가져오기
    const taskContent = taskItem.querySelector(".task-content")
    const taskTitle = taskItem.querySelector(".task-title")
    const originalContent = taskTitle.textContent.trim()

    const originalCategory = taskItem.getAttribute("data-category") || ""
    const originalScope = taskItem.getAttribute("data-scope") || "PRIVATE"

    // 기존 할 일 내용 숨기기
    taskItem.style.display = "none"

    // 수정 폼 생성
    const form = document.createElement("form")
    form.className = "edit-form bg-gray-200 p-4 rounded-lg mb-3"
    form.innerHTML = `
    <div class="flex flex-col gap-2">
      <input type="text" name="category" placeholder="카테고리" class="input input-bordered w-full" value="${originalCategory}" required />
      <input type="text" name="content" placeholder="할 일 내용" class="input input-bordered w-full" value="${originalContent}" required />
      <select name="scope" class="select select-bordered w-full" required>
        <option value="PRIVATE" ${originalScope === "PRIVATE" ? "selected" : ""}>PRIVATE</option>
        <option value="PUBLIC" ${originalScope === "PUBLIC" ? "selected" : ""}>PUBLIC</option>
        <option value="FOLLOWERS" ${originalScope === "FOLLOWERS" ? "selected" : ""}>FOLLOWERS</option>
      </select>
      <div class="flex gap-2 justify-end mt-2">
        <button type="submit" class="btn btn-sm btn-primary">저장</button>
        <button type="button" class="btn btn-sm btn-ghost cancel-edit">취소</button>
      </div>
    </div>
  `

    // 폼 제출 이벤트 처리
    form.addEventListener("submit", async (e) => {
        e.preventDefault()

        const updatedCategory = form.category.value.trim()
        const updatedContent = form.content.value.trim()
        const updatedScope = form.scope.value

        try {
            const response = await authFetch(`/tasks/${taskId}`, {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    category: updatedCategory,
                    content: updatedContent,
                    scope: updatedScope,
                }),
            })

            if (!response.ok) throw new Error("할 일 수정에 실패했습니다.")

            // 수정 성공 시 UI 업데이트
            form.remove()
            taskItem.style.display = ""

            // 최신 할 일 목록 다시 불러오기
            const res = await authFetch(`/tasks/${taskId}`)
            if (!res.ok) {
                alert("할 일 정보를 불러오는 데 실패했습니다.")
                return
            }
            const json = await res.json()
            const task = json.data
            await fetchAndRenderTasks(dueDateToDate(task.dueDate), localStorage.getItem("userId"))
        } catch (err) {
            console.error("수정 실패:", err)
            alert(err.message)
        }
    })

    // 취소 버튼 이벤트 처리
    form.querySelector(".cancel-edit").addEventListener("click", () => {
        form.remove()
        taskItem.style.display = ""
    })

    // 폼을 할 일 항목 바로 뒤에 삽입
    taskItem.parentNode.insertBefore(form, taskItem.nextSibling)
}

async function deleteTask(taskId) {
    const res = await authFetch(`/tasks/${taskId}`)
    if (!res.ok) {
        alert("할 일 정보를 불러오는 데 실패했습니다.")
        return
    }
    const json = await res.json()
    const task = json.data
    if (!task) {
        alert("할 일 정보를 찾을 수 없습니다.")
        return
    }

    if (task.taskImage) {
        alert("완료 인증 이미지가 있는 할 일은 삭제할 수 없습니다.")
        return
    }

    const lastChar = task.content[task.content.length - 1];
    const code = lastChar.charCodeAt(0);
    const fin = (code - 0xac00) % 28;
    const particle = (fin === 0) ? "를":"을";
    const confirmed = confirm(`정말 "${task.content}"${particle} 삭제하시겠습니까?`);

    if (!confirmed) return

    try {
        const res = await authFetch(`/tasks/${taskId}`, {
            method: "DELETE",
        })

        if (!res.ok) throw new Error("삭제 실패")

        await fetchAndRenderTasks(dueDateToDate(task.dueDate), localStorage.getItem("userId"))
        const calendarEl = document.getElementById("calendar");
        if (calendarEl) {
            buildCalendar(calendarEl, localStorage.getItem("userId"));
        } else {
            console.warn("❗ calendar 요소가 없음");
        }
    } catch (err) {
        console.error("삭제 실패:", err)
        alert(err.message)
    }
}

async function uploadImage(taskId) {
    // 먼저 해당 task의 최신 상태를 가져옴
    const res = await authFetch(`/tasks/${taskId}`)
    if (!res.ok) {
        alert("할 일 정보를 불러오는 데 실패했습니다.")
        return
    }

    const json = await res.json()
    const task = json.data
    if (!task) {
        alert("할 일 정보를 찾을 수 없습니다.")
        return
    }
    if (task.status !== "COMPLETE") {
        alert("완료 인증 사진은 할 일을 완료한 후 업로드할 수 있습니다.")
        return
    }

    const input = document.createElement("input")
    input.type = "file"
    input.accept = "image/*"

    input.addEventListener("change", async () => {
        const file = input.files[0]
        if (!file) return

        const formData = new FormData()
        formData.append("image", file)

        try {
            const uploadRes = await authUpload(`/tasks/${taskId}/image`, formData)
            if (!uploadRes.ok) throw new Error("이미지 업로드 실패")

            await fetchAndRenderTasks(dueDateToDate(task.dueDate), localStorage.getItem("userId"))
        } catch (err) {
            console.error("이미지 업로드 실패:", err)
            alert(err.message)
        }
    })

    document.body.appendChild(input)
    input.click()
    document.body.removeChild(input)
}

// 댓글 모달 요소 생성
function createCommentModal() {
    const modal = document.createElement("div")
    modal.id = "commentModal"
    modal.className = "comment-modal hidden"
    modal.innerHTML = `
    <div class="comment-modal-content">
      <div class="comment-modal-header">
        <h3>댓글</h3>
        <button class="close-modal">&times;</button>
      </div>
      <div class="comment-list"></div>
      <div class="comment-form">
        <textarea placeholder="댓글을 입력하세요"></textarea>
        <button class="submit-comment">댓글 작성</button>
      </div>
    </div>
  `

    document.body.appendChild(modal)

    // 모달 닫기 버튼 이벤트
    modal.querySelector(".close-modal").addEventListener("click", () => {
        modal.classList.add("hidden")
    })

    return modal
}

async function fetchComments(taskId) {
    try {
        const res = await authFetch(`/comments/${taskId}`)
        if (!res.ok) throw new Error("댓글을 불러오는데 실패했습니다")

        const data = await res.json()
        return data.data || []
    } catch (err) {
        console.error("댓글 불러오기 실패:", err)
        return []
    }
}

async function addComment(request) {
    try {
        const res = await authFetch(`/comments`, {
            method: "POST",
            body: JSON.stringify(request),
        })

        if (!res.ok) throw new Error("댓글 추가에 실패했습니다")
        return true
    } catch (err) {
        console.error("댓글 추가 실패:", err)
        return false
    }
}

async function renderComments(container, taskId, comments = null) {
    if (!comments) {
        const res = await authFetch(`/comments/${taskId}`)
        if (!res.ok) throw new Error("댓글을 불러오는데 실패했습니다")
        const responseData = await res.json()
        comments = responseData.data
    }

    container.innerHTML = ""

    if (!comments || comments.length === 0) {
        container.innerHTML = '<p class="no-comments">댓글이 없습니다</p>'
        return
    }

    comments.forEach((comment) => {
        const commentEl = createCommentElement(comment, taskId)
        container.appendChild(commentEl)
    })
}

function createCommentElement(comment, taskId, isReply = false) {
    const commentEl = document.createElement("div")
    commentEl.className = isReply ? "reply-item ml-6 border-l pl-4 mt-2" : "comment-item mt-4"
    commentEl.dataset.id = comment.id

    const authorImg =
        comment.profileImage ||
        "data:image/jpeg;base64,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"
    const authorName = comment.nickname || "사용자"

    const currentUserId = localStorage.getItem("userId");
    const isMyComment = currentUserId && comment.userId == currentUserId

    const actionButtons = isMyComment
        ? `
    <div class="comment-actions flex gap-2 ml-auto">
      <button class="edit-comment-btn text-blue-500 hover:underline">수정</button>
      <button class="delete-comment-btn text-red-500 hover:underline">삭제</button>
    </div>`
        : ""

    const replyButton = `<button class="reply-comment-btn text-sm text-gray-500 hover:underline ml-2">답글</button>`

    commentEl.innerHTML = `
    <div class="comment-author flex items-center gap-2">
      <img src="${authorImg}" alt="${authorName}" class="author-img w-8 h-8 rounded-full" />
      <div class="author-info text-sm">
        <span class="author-name font-semibold">${authorName}</span>
        <span class="comment-date text-gray-500 text-xs ml-2">${new Date(comment.createdAt).toLocaleString()}</span>
      </div>
      ${actionButtons}
      ${!isReply ? replyButton : ""}
    </div>
    <div class="comment-content mt-1 text-sm">${comment.content}</div>
    <div class="comment-edit-form hidden mt-1">
      <textarea class="edit-textarea w-full p-2 border rounded">${comment.content}</textarea>
      <div class="edit-actions mt-1 flex gap-2">
        <button class="save-edit-btn text-white bg-blue-500 px-3 py-1 rounded">저장</button>
        <button class="cancel-edit-btn text-gray-500 px-3 py-1 rounded">취소</button>
      </div>
    </div>
    <div class="reply-form hidden mt-2">
      <textarea class="reply-textarea w-full p-2 border rounded" placeholder="답글을 입력하세요"></textarea>
      <button class="submit-reply-btn mt-1 bg-green-500 text-white px-3 py-1 rounded">답글 달기</button>
    </div>
    <div class="reply-container mt-2 pl-4"></div>
  `

    // 이벤트 바인딩 (수정, 삭제, 답글)
    bindCommentEvents(commentEl, comment, taskId)

    // 대댓글 렌더링
    const replyContainer = commentEl.querySelector(".reply-container")
    if (comment.children && comment.children.length > 0) {
        comment.children.forEach((reply) => {
            const replyEl = createCommentElement(reply, taskId, true)
            replyContainer.appendChild(replyEl)
        })
    }

    return commentEl
}

function bindCommentEvents(commentEl, comment, taskId) {
    const currentUserId = localStorage.getItem("userId");
    const isMyComment = currentUserId && comment.userId == currentUserId
    console.log(comment.id);
    console.log(localStorage.getItem("userId"));
    console.log(isMyComment);

    if (isMyComment) {
        // 수정
        const editBtn = commentEl.querySelector(".edit-comment-btn")
        const cancelEditBtn = commentEl.querySelector(".cancel-edit-btn")
        const saveEditBtn = commentEl.querySelector(".save-edit-btn")
        const contentEl = commentEl.querySelector(".comment-content")
        const editFormEl = commentEl.querySelector(".comment-edit-form")

        if (editBtn) {
            editBtn.addEventListener("click", () => {
                contentEl.classList.add("hidden")
                editFormEl.classList.remove("hidden")
            })
        }

        if (cancelEditBtn) {
            cancelEditBtn.addEventListener("click", () => {
                contentEl.classList.remove("hidden")
                editFormEl.classList.add("hidden")
            })
        }

        if (saveEditBtn) {
            saveEditBtn.addEventListener("click", async () => {
                const newContent = commentEl.querySelector(".edit-textarea").value.trim()
                if (!newContent) return

                const res = await authFetch(`/comments/${comment.id}`, {
                    method: "PUT",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ content: newContent }),
                })

                if (res.ok) {
                    contentEl.textContent = newContent
                    contentEl.classList.remove("hidden")
                    editFormEl.classList.add("hidden")
                }
            })
        }

        // 삭제
        const deleteBtn = commentEl.querySelector(".delete-comment-btn")
        if (deleteBtn) {
            deleteBtn.addEventListener("click", async () => {
                if (!confirm("댓글을 삭제하시겠습니까?")) return
                const res = await authFetch(`/comments/${comment.id}`, {
                    method: "DELETE",
                })
                if (res.ok) {
                    commentEl.remove()
                    const comments = await fetchComments(taskId)
                    const totalCount = countCommentsWithReplies(comments)
                    updateCommentCount(taskId, totalCount)
                }
            })
        }
    }

    // 답글 폼 열기
    const replyBtn = commentEl.querySelector(".reply-comment-btn")
    const replyForm = commentEl.querySelector(".reply-form")
    const replyTextarea = commentEl.querySelector(".reply-textarea")
    const submitReplyBtn = commentEl.querySelector(".submit-reply-btn")
    const replyContainer = commentEl.querySelector(".reply-container")

    if (replyBtn) {
        replyBtn.addEventListener("click", () => {
            replyForm.classList.toggle("hidden")
        })
    }

    // 답글 저장
    if (submitReplyBtn) {
        submitReplyBtn.addEventListener("click", async () => {
            const replyContent = replyTextarea.value.trim()
            if (!replyContent) return

            const res = await authFetch(`/comments`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    taskId,
                    parentId: comment.id, // 대댓글
                    content: replyContent,
                }),
            })
            if (res.ok) {
                const newReply = (await res.json()).data
                const replyEl = createCommentElement(newReply, taskId, true)
                replyContainer.appendChild(replyEl)
                replyTextarea.value = ""
                replyForm.classList.add("hidden")
                const comments = await fetchComments(taskId)
                const totalCount = countCommentsWithReplies(comments)
                updateCommentCount(taskId, totalCount)
            }
        })
    }
}

// 댓글 카운트 업데이트 함수
function updateCommentCount(taskId, count) {
    const taskItem = document.getElementById(`task-${taskId}`)
    if (!taskItem) return

    const commentCount = taskItem.querySelector(".comment-count")
    if (commentCount) {
        commentCount.textContent = count
    }
}

// 댓글 기능 초기화
export function initCommentFeature() {
    // 모달 생성
    const modal = createCommentModal()
    const commentList = modal.querySelector(".comment-list")
    const commentForm = modal.querySelector(".comment-form")
    const commentInput = commentForm.querySelector("textarea")
    const submitButton = commentForm.querySelector(".submit-comment")

    let currentTaskId = null

    // 댓글 아이콘 클릭 이벤트 처리
    document.addEventListener("click", async (e) => {
        // 댓글 아이콘 클릭 감지
        if (e.target.closest(".comment-icon")) {
            const taskItem = e.target.closest(".task-item")
            if (!taskItem) return

            const taskId = taskItem.id.replace("task-", "")
            currentTaskId = taskId

            // 댓글 불러오기
            const comments = await fetchComments(taskId)
            await renderComments(commentList, taskId, comments)

            // 모달 표시
            modal.classList.remove("hidden")
        }
    })

    // 댓글 제출 이벤트
    submitButton.addEventListener("click", async () => {
        const content = commentInput.value.trim()
        if (!content || !currentTaskId) return

        // CommentCreateRequestDto 형식에 맞게 데이터 구성
        const request = {
            content: content,
            taskId: currentTaskId,
        }

        const success = await addComment(request)
        if (success) {
            commentInput.value = ""

            // 댓글 다시 불러오기
            const comments = await fetchComments(currentTaskId)
            const totalCount = countCommentsWithReplies(comments)
            await renderComments(commentList, currentTaskId, comments)
            updateCommentCount(currentTaskId, totalCount)
        }
    })

    // 엔터키로 댓글 제출
    commentInput.addEventListener("keydown", (e) => {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault()
            submitButton.click()
        }
    })
}

function countCommentsWithReplies(comments) {
    let count = 0
    for (let i = 0; i < comments.length; i++) {
        count++ // 부모 댓글 1개
        if (comments[i].children && comments[i].children.length > 0) {
            count += comments[i].children.length // 대댓글 개수 추가
        }
    }
    return count
}

async function refreshCommentCount(taskId) {
    const comments = await fetchComments(taskId)
    const totalCount = countCommentsWithReplies(comments)
    updateCommentCount(taskId, totalCount)
    return totalCount
}

async function postponeDueDate(taskId) {
    const res = await authFetch(`/tasks/${taskId}`)
    if (!res.ok) {
        alert("할 일 정보를 불러오는 데 실패했습니다.")
        return
    }
    const json = await res.json()
    const task = json.data
    if (!task) {
        alert("할 일 정보를 찾을 수 없습니다.")
        return
    }

    if (task.status === "COMPLETE") {
        alert("완료한 일은 미룰 수 없습니다")
        return
    }

    const due = new Date(task.dueDate)
    due.setHours(due.getHours() + 24)
    task.dueDate = due.toISOString()
    try {
        const res = await authFetch(`/tasks/${taskId}/postpone`, {
            method: "PATCH",
        })

        if (!res.ok) throw new Error("미루기 실패")

        const due = new Date(task.dueDate)
        due.setHours(due.getHours() - 24)
        const targetDate = due.toISOString()
        await fetchAndRenderTasks(dueDateToDate(targetDate), localStorage.getItem("userId"))
    } catch (err) {
        console.error("미루기 실패:", err)
        alert(err.message)
    }
}
