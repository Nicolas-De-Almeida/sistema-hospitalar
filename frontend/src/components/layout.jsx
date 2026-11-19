import SideBar from './sidebar'
import TopBar from './topbar'

export default function Layout({ children }) {
    return (
        <div className="flex h-screen">
            <SideBar />
            {/* min-w-0 deixa esta coluna encolher em vez de empurrar o menu */}
            <div className="flex flex-col flex-1 min-w-0">
                <TopBar />
                <main className="flex-1 overflow-auto p-6 bg-zinc-50">
                    {children}
                </main>
            </div>
        </div>
    )
}