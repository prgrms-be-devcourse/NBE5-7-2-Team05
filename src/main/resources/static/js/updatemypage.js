document.addEventListener("DOMContentLoaded", async () => {
    const userId = localStorage.getItem("userId");

    console.log(userId);

    if (!userId) {
        alert("로그인이 필요합니다.");
        window.location.href = "/login";
        return;
    }

    const previewImg = document.getElementById("preview-img");
    const imageInput = document.getElementById("image-input");

    // ✅ 유저 정보 불러오기 (accessToken은 쿠키로 자동 전송됨)
    try {
        const res = await fetch(`/users/${userId}`, {
            method: "GET",
            credentials: "include"
        });

        const json = await res.json();
        const user = json.data;
        if (!res.ok || !user) throw new Error("유저 정보 없음");

        document.getElementById("nickname").value = user.nickname || "";
        document.getElementById("intro").value = user.intro || "";
        previewImg.src = user.profileImage || "/images/default-profile.png";
    } catch (err) {
        console.error("유저 정보 불러오기 실패:", err);
        alert("유저 정보를 불러오지 못했습니다.");
    }

    // ✅ 이미지 미리보기
    imageInput.addEventListener("change", () => {
        const file = imageInput.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = e => {
                previewImg.src = e.target.result;
            };
            reader.readAsDataURL(file);
        }
    });

    // ✅ 이미지 삭제
    document.querySelector(".image-delete-button").addEventListener("click", () => {
        previewImg.src = "/images/default-profile.png";
        imageInput.value = "";
    });

    // ✅ 수정 버튼
    document.getElementById("save-btn").addEventListener("click", async () => {
        const nickname = document.getElementById("nickname").value;
        const intro = document.getElementById("intro").value;
        const profileImage = previewImg.src;

        const body = { nickname, intro, profileImage };

        try {
            const res = await fetch(`/users/${userId}`, {
                method: "PATCH",
                credentials: "include",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(body)
            });

            if (!res.ok) throw new Error("수정 실패");

            alert("프로필이 수정되었습니다.");
            window.location.href = "/mypage";
        } catch (err) {
            console.error("프로필 수정 오류:", err);
            alert("수정 중 오류가 발생했습니다.");
        }
    });
});
