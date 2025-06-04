import http from 'k6/http';
import { check } from 'k6';

// 설정: 가상 사용자 수와 테스트 시간
export let options = {
  vus: 10,
  duration: '10s',
};

// JSON 부분: 매 요청마다 고유한 username을 생성
const makeJsonPart = () => JSON.stringify({
  username: `loadtest_user_${Math.floor(Math.random() * 1000000)}`,
  password: 'test1234!',
  nickname: 'loadtester',
  email: `user${Math.floor(Math.random() * 1000000)}@test.com`
});

// 바이너리 이미지 파일 (sample.jpg는 같은 디렉토리에 있어야 함)
const binaryFile = open('./sample.jpg', 'b');

export default function () {
  const boundary = '----WebKitFormBoundary7MA4YWxkTrZu0gW'; // 수동 boundary
  const jsonPart = makeJsonPart();

  // 멀티파트 형식 조립
  const payload =
      `--${boundary}\r\n` +
      `Content-Disposition: form-data; name="userCreateRequest"\r\n` +
      `Content-Type: application/json\r\n\r\n` +
      `${jsonPart}\r\n` +
      `--${boundary}\r\n` +
      `Content-Disposition: form-data; name="profile"; filename="sample.jpg"\r\n` +
      `Content-Type: image/jpeg\r\n\r\n` +
      `${binaryFile}\r\n` +
      `--${boundary}--\r\n`;

  const headers = {
    'Content-Type': `multipart/form-data; boundary=${boundary}`,
  };

  const url = 'http://localhost:8080/api/users';
  const res = http.post(url, payload, { headers });

  // 응답 상태 코드 출력
  console.log(`Response status: ${res.status}`);
  console.log(`Response body: ${res.body.substring(0, 200)}...`); // 응답 본문 일부만 출력

  // 요청 성공 여부 체크
  check(res, {
    'status is 201': (r) => r.status === 201,
  });
}
