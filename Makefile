start:
	cd backend && ./mvnw spring-boot:run &
	cd frontend && npm install && npm run dev

stop:
	pkill -f "spring-boot:run"

restart:
	pkill -f "spring-boot:run"
	cd backend && ./mvnw spring-boot:run &