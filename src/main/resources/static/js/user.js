document.addEventListener("DOMContentLoaded", async () => {
    const params = new URLSearchParams(window.location.search);
    const userId = params.get("userId");

    if (!userId) {
        alert("유저 ID가 없습니다.");
        return;
    }

    try {
        const res = await fetch(`/users/${userId}`);
        if (!res.ok) throw new Error("프로필 조회 실패");

        const responseBody = await res.json();
        const user = responseBody.data; // BaseResponse 내부의 data

        const profileHeader = document.getElementById("userProfileHeader");

        // 유저 프로필 이미지 + 닉네임 동적으로 삽입
        profileHeader.innerHTML = `
            <img src="${user.profileImage ?? "/images/default-profile.png"}" 
                 alt="프로필 이미지" 
                 class="w-12 h-12 rounded-full object-cover border" />
            <span class="text-xl font-semibold">${user.nickname}</span>
        `;


        // 팔로우 상태 확인
        const followRes = await fetch(`/api/follow/status?from=${currentUserId}&to=${userId}`);
        const followStatus = await followRes.json();
        const isFollowing = followStatus.data.following;

        const followBtn = document.getElementById("followToggleBtn");
        followBtn.textContent = isFollowing ? "언팔로우" : "팔로우";

        followBtn.addEventListener("click", async () => {
            const method = isFollowing ? "DELETE" : "POST";
            const followUrl = `/api/follow?from=${currentUserId}&to=${userId}`;

            const followToggleRes = await fetch(followUrl, {
                method,
                headers: { 'Content-Type': 'application/json' }
            });

            if (followToggleRes.ok) {
                alert(isFollowing ? "언팔로우 했습니다." : "팔로우 했습니다.");
                location.reload(); // 버튼 상태 갱신
            } else {
                alert("요청 실패");
            }
        });
    } catch (err) {
        console.error(err);
        alert("프로필 정보를 불러오는 데 실패했습니다.");
    }
});
