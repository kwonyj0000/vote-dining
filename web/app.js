// 메뉴 목록을 서버에서 받아 화면에 렌더링
async function loadMenus() {
  const res = await fetch('/api/menus');
  const menus = await res.json();
  const list = document.getElementById('menu-list');

  if (menus.length === 0) {
    list.innerHTML = '<li class="empty">아직 등록된 메뉴가 없습니다.</li>';
    return;
  }

  list.innerHTML = menus.map((m, i) => `
    <li>
      <div class="menu-info">
        <span class="rank">${i + 1}</span>
        <span class="menu-name">${escapeHtml(m.name)}</span>
        <span class="vote-count">${m.votes}표</span>
      </div>
      <div class="action-btns">
        <button class="vote-btn" onclick="vote('${escapeAttr(m.name)}')">투표</button>
        <button class="del-btn" onclick="deleteMenu('${escapeAttr(m.name)}')">삭제</button>
      </div>
    </li>
  `).join('');
}

// 메뉴 등록
async function addMenu() {
  const input = document.getElementById('menu-input');
  const name = input.value.trim();
  if (!name) return;

  await fetch('/api/menus', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ name }),
  });

  input.value = '';
  loadMenus();
}

// 특정 메뉴에 투표
async function vote(name) {
  await fetch(`/api/vote/${encodeURIComponent(name)}`, { method: 'POST' });
  loadMenus();
}

// 메뉴 삭제
async function deleteMenu(name) {
  await fetch(`/api/delete/${encodeURIComponent(name)}`, { method: 'DELETE' });
  loadMenus();
}

// 투표 1위 메뉴 + 입력한 지역으로 네이버 지도 맛집 검색 탭 열기
async function openNaverMap() {
  const res = await fetch('/api/menus');
  const menus = await res.json();
  if (menus.length === 0) {
    alert('먼저 메뉴를 등록하고 투표해주세요.');
    return;
  }

  const topMenu = menus[0].name;
  const location = document.getElementById('location-input').value.trim();
  const query = location ? `${topMenu} ${location} 맛집` : `${topMenu} 맛집`;
  window.open(`https://map.naver.com/v5/search/${encodeURIComponent(query)}`, '_blank');
}

// XSS 방지: HTML 특수문자 이스케이프
function escapeHtml(str) {
  return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

// 인라인 이벤트 속성에 들어가는 문자열 이스케이프
function escapeAttr(str) {
  return str.replace(/'/g, "\\'");
}

// 페이지 로드 시 목록 표시
loadMenus();

// 3초마다 폴링으로 실시간 순위 갱신
setInterval(loadMenus, 3000);

// Enter 키로 메뉴 등록
document.getElementById('menu-input').addEventListener('keydown', (e) => {
  if (e.key === 'Enter') addMenu();
});

// Enter 키로 맛집 검색
document.getElementById('location-input').addEventListener('keydown', (e) => {
  if (e.key === 'Enter') openNaverMap();
});
