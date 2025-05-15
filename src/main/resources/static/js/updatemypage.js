document.addEventListener("DOMContentLoaded", async () => {
    const accessToken = localStorage.getItem("accessToken");

    if (!accessToken) {
        alert("로그인이 필요합니다.");
        window.location.href = "/login";
        return;
    }

    // ✅ accessToken에서 userId(sub) 추출
    let userId = null;
    try {
        const payload = JSON.parse(atob(accessToken.split('.')[1]));
        userId = payload.sub;
    } catch (err) {
        console.error("토큰 디코딩 실패", err);
        alert("유저 정보를 불러올 수 없습니다.");
        window.location.href = "/login";
        return;
    }

    const previewImg = document.getElementById("preview-img");
    const imageInput = document.getElementById("image-input");

    // ✅ 유저 정보 불러오기
    try {
        const res = await fetch(`/users/${userId}`, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${accessToken}`,
                "Content-Type": "application/json"
            },
            credentials: "include"
        });

        const json = await res.json();
        const result = json?.result;

        if (!res.ok || !result) throw new Error("유저 정보 불러오기 실패");

        document.getElementById("nickname").value = result.nickname || "";
        document.getElementById("email").value = result.email || "";  // ✅ 여기 id="" → "email"로 수정
        document.getElementById("intro").value = result.intro || "";
        previewImg.src = result.profileImage || "/images/default-profile.png";

    } catch (err) {
        console.error("유저 정보를 불러오지 못했습니다.", err);
        alert("유저 정보를 불러오지 못했습니다.");
    }

    // ✅ 이미지 미리보기 처리
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

    // ✅ 이미지 삭제 버튼
    document.querySelector(".image-delete-button").addEventListener("click", () => {
        previewImg.src = "/images/default-profile.png";
        imageInput.value = "";
    });

    // ✅ 프로필 수정 버튼
    document.getElementById("save-btn").addEventListener("click", async () => {
        const nickname = document.getElementById("nickname").value;
        const intro = document.getElementById("intro").value;
        const profileImage = previewImg.src;

        const body = {
            nickname,
            intro,
            profileImage
        };

        try {
            const res = await fetch(`/users/${userId}`, {
                method: "PATCH",
                headers: {
                    "Authorization": `Bearer ${accessToken}`,
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(body),
                credentials: "include"
            });

            if (!res.ok) throw new Error("수정 실패");

            alert("프로필이 수정되었습니다.");
            location.href = "/mypage";
        } catch (err) {
            console.error("수정 중 오류 발생", err);
            alert("수정 중 오류가 발생했습니다.");
        }
    });
});
